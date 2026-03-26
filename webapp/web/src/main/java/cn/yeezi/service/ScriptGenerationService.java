package cn.yeezi.service;

import cn.yeezi.ai.AiSceneType;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.entity.PromptEntity;
import cn.yeezi.db.entity.ScriptAssetEntity;
import cn.yeezi.db.entity.ScriptGenerateBatchEntity;
import cn.yeezi.db.entity.ScriptGenerateHistoryEntity;
import cn.yeezi.db.repository.ScriptAssetRepository;
import cn.yeezi.db.repository.ScriptGenerateBatchRepository;
import cn.yeezi.db.repository.ScriptGenerateHistoryRepository;
import cn.yeezi.model.param.ScriptGenerateParam;
import cn.yeezi.model.param.ScriptRerunParam;
import cn.yeezi.model.vo.ScriptGenerateVO;
import cn.yeezi.service.llm.LlmChatResponse;
import cn.yeezi.service.llm.LlmService;
import cn.yeezi.web.WebSessionHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScriptGenerationService {

    private static final String SOURCE_GENERATE = "GENERATE";
    private static final String SOURCE_RERUN = "RERUN";
    private static final String SOURCE_FEEDBACK_RERUN = "FEEDBACK_RERUN";
    private static final int BATCH_STATUS_PROCESSING = 0;
    private static final int BATCH_STATUS_SUCCESS = 1;
    private static final int BATCH_STATUS_PARTIAL_FAIL = 2;
    private static final int BATCH_STATUS_FAILED = 3;

    private final LlmService llmService;
    private final ProductService productService;
    private final RuleService ruleService;
    private final TagService tagService;
    private final PromptService promptService;
    private final OpeningStrategyAssignmentService openingStrategyAssignmentService;
    private final ScriptGenerationPromptBuilder scriptGenerationPromptBuilder;
    private final ScriptGenerationLlmResponseParser scriptGenerationLlmResponseParser;
    private final ScriptAssetRepository scriptAssetRepository;
    private final ScriptGenerateBatchRepository scriptGenerateBatchRepository;
    private final ScriptGenerateHistoryRepository scriptGenerateHistoryRepository;
    private final ObjectMapper objectMapper;
    @Qualifier("scriptGenerateExecutor")
    private final Executor scriptGenerateExecutor;

    @Transactional
    public List<ScriptGenerateVO> generate(ScriptGenerateParam param) {
        // 说明：串联产品校验、标签解析、提示词组装、规则与案例快照、Dify 调用
        Long userId = param.getUserId() != null ? param.getUserId() : WebSessionHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        }
        param.setUserId(userId);
        int adNumber = param.getAdNumber() == null ? 1 : param.getAdNumber();
        if (adNumber < 1 || adNumber > 10) {
            throw new BusinessException("广告脚本生成条数需在1到10之间");
        }

        ScriptGenerateBatchEntity batch = createBatch(
                param.getProductId(),
                userId,
                param.getPromptId(),
                adNumber,
                SOURCE_GENERATE,
                writeJson(param)
        );
        List<OpeningStrategy> openingStrategies = openingStrategyAssignmentService.assignStrategies(adNumber);

        List<CompletableFuture<GenerateItemResult>> futures = new ArrayList<>();
        for (int i = 1; i <= adNumber; i++) {
            final int itemSeq = i;
            final OpeningStrategy openingStrategy = openingStrategies.get(i - 1);
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    ScriptGenerateVO result = generateOnce(param, itemSeq, adNumber, openingStrategy);
                    return GenerateItemResult.success(itemSeq, result);
                } catch (Exception e) {
                    return GenerateItemResult.fail(itemSeq, resolveErrorMessage(e));
                }
            }, scriptGenerateExecutor));
        }

        List<GenerateItemResult> itemResults = futures.stream()
                .map(CompletableFuture::join)
                .sorted((o1, o2) -> Integer.compare(o1.getItemSeq(), o2.getItemSeq()))
                .collect(Collectors.toList());

        List<ScriptGenerateVO> successList = new ArrayList<>();
        String firstError = null;
        int failCount = 0;
        for (GenerateItemResult itemResult : itemResults) {
            if (itemResult.isSuccess()) {
                ScriptGenerateHistoryEntity history = saveHistorySuccess(batch.getId(), itemResult.getItemSeq(), itemResult.getResult(), SOURCE_GENERATE, userId);
                ScriptGenerateVO result = itemResult.getResult();
                result.setBatchId(batch.getId());
                result.setHistoryId(history.getId());
                result.setItemSeq(itemResult.getItemSeq());
                successList.add(result);
                continue;
            }
            failCount++;
            if (firstError == null) {
                firstError = itemResult.getErrorMsg();
            }
            saveHistoryFailure(
                    batch.getId(),
                    itemResult.getItemSeq(),
                    param.getProductId(),
                    userId,
                    param.getPromptId(),
                    SOURCE_GENERATE,
                    itemResult.getErrorMsg(),
                    writeJson(param)
            );
        }

        int successCount = successList.size();
        finishBatch(batch.getId(), successCount, failCount);
        if (failCount > 0) {
            throw new BusinessException(firstError == null ? "脚本生成失败" : firstError);
        }
        return successList;
    }

    private ScriptGenerateVO generateOnce(
            ScriptGenerateParam param,
            Integer itemSeq,
            Integer batchSize,
            OpeningStrategy openingStrategy
    ) {
        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        List<Long> tagIds = tagService.resolveTagIds(param.getProductId(), param.getTags());
        tagService.validateRequiredTagSelections(param.getProductId(), tagIds);
        Map<String, List<String>> tagValues = tagService.resolveTagValuesByGroupCode(param.getProductId(), tagIds);
        String userTagSnapshot = tagService.buildUserTagSnapshot(param.getProductId(), tagIds, param.getControlParams());

        PromptEntity prompt = promptService.getActivePrompt(param.getProductId(), param.getPromptId());
        List<String> caseSnippets = resolveExcellentCaseSnippet(param.getProductId(), param.getExcellentCaseId());
        String ruleSnapshot = ruleService.buildRuleSnapshot(param.getProductId(), tagIds);
        ScriptGenerationPromptContext promptContext = buildPromptContext(
                param,
                product,
                prompt,
                ruleSnapshot,
                caseSnippets,
                tagValues,
                itemSeq,
                batchSize,
                openingStrategy
        );
        ScriptGenerationPrompt promptContent = scriptGenerationPromptBuilder.buildGeneratePrompt(promptContext);
        ScriptGenerationLlmResult llmResult = generateScript(promptContent);

        ScriptGenerateVO vo = new ScriptGenerateVO();
        vo.setAssetId(null);
        vo.setProductId(param.getProductId());
        vo.setPromptId(prompt.getId());
        vo.setPromptSnapshot(promptContent.getPromptSnapshot());
        vo.setRuleSnapshot(ruleSnapshot);
        vo.setInputSnapshot(writeJson(param));
        vo.setTagSnapshot(writeJson(tagIds));
        vo.setUserInputSnapshot(userTagSnapshot);
        vo.setCaseSnapshot(writeJson(caseSnippets));
        vo.setModelName(llmService.getCurrentModel(AiSceneType.SCRIPT_GENERATION));
        vo.setRouteStrategy("DEFAULT");
        vo.setOutputContent(llmResult.getScript());
        vo.setParentAssetId(null);
        vo.setRevisionSeq(0);
        return vo;
    }

    @Transactional
    public ScriptGenerateVO rerunFromAsset(ScriptAssetEntity asset, String instruction) {
        String inputSnapshot = asset.getInputSnapshot();
        ScriptGenerateParam param = parseInputSnapshot(inputSnapshot);
        if (param == null) {
            throw new BusinessException("输入快照缺失");
        }
        Long userId = param.getUserId() != null ? param.getUserId() : asset.getUserId();
        param.setUserId(userId);
        ScriptGenerateVO result = rerunWithSnapshot(
                param,
                asset.getPromptSnapshot(),
                inputSnapshot,
                asset.getOutputContent(),
                instruction
        );
        result.setParentAssetId(asset.getId());
        result.setRevisionSeq(asset.getRevisionSeq() == null ? 1 : asset.getRevisionSeq() + 1);
        return result;
    }

    @Transactional
    public ScriptGenerateVO rerun(ScriptRerunParam param) {
        if (param.getAssetId() != null) {
            ScriptAssetEntity asset = scriptAssetRepository.getById(param.getAssetId());
            if (asset == null || Boolean.TRUE.equals(asset.getDel())) {
                throw new BusinessException("资产不存在");
            }
            ScriptGenerateVO result = rerunFromAsset(asset, param.getAdditionalInstruction());
            Long historyUserId = resolveHistoryUserId(result.getInputSnapshot(), asset.getUserId());
            recordSingleHistory(result, historyUserId, SOURCE_RERUN, writeJson(param));
            return result;
        }
        if (param.getInputSnapshot() == null || param.getInputSnapshot().isBlank()) {
            throw new BusinessException("输入快照缺失");
        }
        if (param.getOutputContent() == null || param.getOutputContent().isBlank()) {
            throw new BusinessException("当前脚本内容不能为空");
        }
        ScriptGenerateParam baseParam = parseInputSnapshot(param.getInputSnapshot());
        if (baseParam == null) {
            throw new BusinessException("输入快照缺失");
        }
        Long userId = baseParam.getUserId() != null ? baseParam.getUserId() : WebSessionHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        }
        baseParam.setUserId(userId);
        ScriptGenerateVO result = rerunWithSnapshot(
                baseParam,
                param.getPromptSnapshot(),
                param.getInputSnapshot(),
                param.getOutputContent(),
                param.getAdditionalInstruction()
        );
        result.setParentAssetId(null);
        result.setRevisionSeq(0);
        recordSingleHistory(result, userId, SOURCE_RERUN, writeJson(param));
        return result;
    }

    public String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private ScriptGenerateParam parseInputSnapshot(String snapshot) {
        if (snapshot == null || snapshot.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(snapshot, ScriptGenerateParam.class);
        } catch (Exception e) {
            return null;
        }
    }

    private ScriptGenerateVO rerunWithSnapshot(
            ScriptGenerateParam param,
            String basePromptSnapshot,
            String inputSnapshot,
            String outputContent,
            String instruction
    ) {
        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        List<Long> tagIds = tagService.resolveTagIds(param.getProductId(), param.getTags());
        tagService.validateRequiredTagSelections(param.getProductId(), tagIds);
        Map<String, List<String>> tagValues = tagService.resolveTagValuesByGroupCode(param.getProductId(), tagIds);
        String userTagSnapshot = tagService.buildUserTagSnapshot(param.getProductId(), tagIds, param.getControlParams());

        PromptEntity prompt = promptService.getActivePrompt(param.getProductId(), param.getPromptId());
        List<String> caseSnippets = resolveExcellentCaseSnippet(param.getProductId(), param.getExcellentCaseId());
        String ruleSnapshot = ruleService.buildRuleSnapshot(param.getProductId(), tagIds);
        ScriptGenerationPromptContext promptContext = buildPromptContext(
                param,
                product,
                prompt,
                ruleSnapshot,
                caseSnippets,
                tagValues,
                null,
                null,
                null
        );
        ScriptGenerationPrompt promptContent = scriptGenerationPromptBuilder.buildRerunPrompt(
                promptContext,
                basePromptSnapshot,
                inputSnapshot,
                outputContent,
                instruction
        );
        ScriptGenerationLlmResult llmResult = generateScript(promptContent);

        ScriptGenerateVO vo = new ScriptGenerateVO();
        vo.setAssetId(null);
        vo.setProductId(param.getProductId());
        vo.setPromptId(prompt.getId());
        vo.setPromptSnapshot(promptContent.getPromptSnapshot());
        vo.setRuleSnapshot(ruleSnapshot);
        vo.setInputSnapshot(inputSnapshot);
        vo.setTagSnapshot(writeJson(tagIds));
        vo.setUserInputSnapshot(userTagSnapshot);
        vo.setCaseSnapshot(writeJson(caseSnippets));
        vo.setModelName(llmService.getCurrentModel(AiSceneType.SCRIPT_GENERATION));
        vo.setRouteStrategy("DEFAULT");
        vo.setOutputContent(llmResult.getScript());
        return vo;
    }

    public ScriptGenerateVO rerunFromAssetWithFeedback(
            ScriptAssetEntity asset,
            List<String> reasonCodes,
            String suggestion
    ) {
        String inputSnapshot = asset.getInputSnapshot();
        ScriptGenerateParam param = parseInputSnapshot(inputSnapshot);
        if (param == null) {
            throw new BusinessException("输入快照缺失");
        }
        Long userId = param.getUserId() != null ? param.getUserId() : asset.getUserId();
        param.setUserId(userId);
        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        List<Long> tagIds = tagService.resolveTagIds(param.getProductId(), param.getTags());
        tagService.validateRequiredTagSelections(param.getProductId(), tagIds);
        Map<String, List<String>> tagValues = tagService.resolveTagValuesByGroupCode(param.getProductId(), tagIds);
        String userTagSnapshot = tagService.buildUserTagSnapshot(param.getProductId(), tagIds, param.getControlParams());

        PromptEntity prompt = promptService.getActivePrompt(param.getProductId(), param.getPromptId());
        List<String> caseSnippets = resolveExcellentCaseSnippet(param.getProductId(), param.getExcellentCaseId());
        String ruleSnapshot = ruleService.buildRuleSnapshot(param.getProductId(), tagIds);
        ScriptGenerationPromptContext promptContext = buildPromptContext(
                param,
                product,
                prompt,
                ruleSnapshot,
                caseSnippets,
                tagValues,
                null,
                null,
                null
        );
        ScriptGenerationPrompt promptContent = scriptGenerationPromptBuilder.buildFeedbackRerunPrompt(
                promptContext,
                asset.getPromptSnapshot(),
                inputSnapshot,
                asset.getOutputContent(),
                reasonCodes,
                suggestion
        );
        ScriptGenerationLlmResult llmResult = generateScript(promptContent);

        ScriptGenerateVO result = new ScriptGenerateVO();
        result.setAssetId(asset.getId());
        result.setProductId(param.getProductId());
        result.setPromptId(prompt.getId());
        result.setPromptSnapshot(promptContent.getPromptSnapshot());
        result.setRuleSnapshot(ruleSnapshot);
        result.setInputSnapshot(inputSnapshot);
        result.setTagSnapshot(writeJson(tagIds));
        result.setUserInputSnapshot(userTagSnapshot);
        result.setCaseSnapshot(writeJson(caseSnippets));
        result.setModelName(llmService.getCurrentModel(AiSceneType.SCRIPT_GENERATION));
        result.setRouteStrategy("DEFAULT");
        result.setOutputContent(llmResult.getScript());
        result.setParentAssetId(asset.getParentAssetId());
        result.setRevisionSeq(asset.getRevisionSeq());
        Map<String, Object> requestSnapshot = new LinkedHashMap<>();
        requestSnapshot.put("assetId", asset.getId());
        requestSnapshot.put("reasonCodes", reasonCodes);
        requestSnapshot.put("suggestion", suggestion);
        recordSingleHistory(result, userId, SOURCE_FEEDBACK_RERUN, writeJson(requestSnapshot));
        return result;
    }

    private void recordSingleHistory(ScriptGenerateVO result, Long userId, String sourceType, String requestSnapshot) {
        ScriptGenerateBatchEntity batch = createBatch(
                result.getProductId(),
                userId,
                result.getPromptId(),
                1,
                sourceType,
                requestSnapshot
        );
        ScriptGenerateHistoryEntity history = saveHistorySuccess(batch.getId(), 1, result, sourceType, userId);
        result.setBatchId(batch.getId());
        result.setHistoryId(history.getId());
        result.setItemSeq(1);
        finishBatch(batch.getId(), 1, 0);
    }

    private ScriptGenerateBatchEntity createBatch(
            Long productId,
            Long userId,
            Long promptId,
            Integer adNumber,
            String sourceType,
            String requestSnapshot
    ) {
        ScriptGenerateBatchEntity batch = new ScriptGenerateBatchEntity();
        batch.setProductId(productId);
        batch.setUserId(userId);
        batch.setPromptId(promptId);
        batch.setAdNumber(adNumber);
        batch.setSourceType(sourceType);
        batch.setSuccessCount(0);
        batch.setFailCount(0);
        batch.setStatus(BATCH_STATUS_PROCESSING);
        batch.setRequestSnapshot(requestSnapshot);
        scriptGenerateBatchRepository.save(batch);
        return batch;
    }

    private void finishBatch(Long batchId, int successCount, int failCount) {
        ScriptGenerateBatchEntity update = new ScriptGenerateBatchEntity();
        update.setId(batchId);
        update.setSuccessCount(successCount);
        update.setFailCount(failCount);
        if (failCount == 0) {
            update.setStatus(BATCH_STATUS_SUCCESS);
        } else if (successCount == 0) {
            update.setStatus(BATCH_STATUS_FAILED);
        } else {
            update.setStatus(BATCH_STATUS_PARTIAL_FAIL);
        }
        scriptGenerateBatchRepository.updateById(update);
    }

    private ScriptGenerateHistoryEntity saveHistorySuccess(
            Long batchId,
            Integer itemSeq,
            ScriptGenerateVO result,
            String sourceType,
            Long userId
    ) {
        ScriptGenerateHistoryEntity history = new ScriptGenerateHistoryEntity();
        history.setBatchId(batchId);
        history.setItemSeq(itemSeq);
        history.setProductId(result.getProductId());
        history.setUserId(userId);
        history.setPromptId(result.getPromptId());
        history.setPromptSnapshot(result.getPromptSnapshot());
        history.setRuleSnapshot(result.getRuleSnapshot());
        history.setInputSnapshot(result.getInputSnapshot());
        history.setTagSnapshot(result.getTagSnapshot());
        history.setUserInputSnapshot(result.getUserInputSnapshot());
        history.setCaseSnapshot(result.getCaseSnapshot());
        history.setModelName(result.getModelName());
        history.setRouteStrategy(result.getRouteStrategy());
        history.setOutputContent(result.getOutputContent());
        history.setSourceType(sourceType);
        history.setParentAssetId(result.getParentAssetId());
        history.setRevisionSeq(result.getRevisionSeq());
        history.setSaveStatus(false);
        history.setSavedAssetId(null);
        history.setErrorMsg(null);
        scriptGenerateHistoryRepository.save(history);
        return history;
    }

    private void saveHistoryFailure(
            Long batchId,
            Integer itemSeq,
            Long productId,
            Long userId,
            Long promptId,
            String sourceType,
            String errorMsg,
            String inputSnapshot
    ) {
        ScriptGenerateHistoryEntity history = new ScriptGenerateHistoryEntity();
        history.setBatchId(batchId);
        history.setItemSeq(itemSeq);
        history.setProductId(productId);
        history.setUserId(userId);
        history.setPromptId(promptId);
        history.setInputSnapshot(inputSnapshot);
        history.setSourceType(sourceType);
        history.setSaveStatus(false);
        history.setSavedAssetId(null);
        history.setErrorMsg(errorMsg);
        scriptGenerateHistoryRepository.save(history);
    }

    private Long resolveHistoryUserId(String inputSnapshot, Long fallbackUserId) {
        ScriptGenerateParam snapshotParam = parseInputSnapshot(inputSnapshot);
        if (snapshotParam != null && snapshotParam.getUserId() != null) {
            return snapshotParam.getUserId();
        }
        Long sessionUserId = WebSessionHolder.getUserId();
        if (sessionUserId != null) {
            return sessionUserId;
        }
        return fallbackUserId;
    }

    private String resolveErrorMessage(Exception e) {
        if (e instanceof BusinessException) {
            return e.getMessage();
        }
        return "脚本生成失败，请稍后重试";
    }

    private List<String> resolveExcellentCaseSnippet(Long productId, Long excellentCaseAssetId) {
        if (excellentCaseAssetId == null) {
            return List.of();
        }
        ScriptAssetEntity asset = scriptAssetRepository.getById(excellentCaseAssetId);
        if (asset == null || Boolean.TRUE.equals(asset.getDel())) {
            throw new BusinessException("优秀案例不存在");
        }
        if (!asset.getProductId().equals(productId)) {
            throw new BusinessException("优秀案例不存在");
        }
        Integer copyCount = asset.getCopyCount();
        if (copyCount == null || copyCount <= 0) {
            throw new BusinessException("优秀案例不存在");
        }
        String snapshot = buildCaseSnapshot(asset.getOutputContent());
        if (snapshot == null || snapshot.isBlank()) {
            return List.of();
        }
        return List.of(snapshot);
    }

    private String buildCaseSnapshot(String outputContent) {
        if (outputContent == null) {
            return null;
        }
        int maxLength = 500;
        if (outputContent.length() <= maxLength) {
            return outputContent;
        }
        return outputContent.substring(0, maxLength);
    }

    private ScriptGenerationPromptContext buildPromptContext(
            ScriptGenerateParam param,
            ProductEntity product,
            PromptEntity prompt,
            String ruleSnapshot,
            List<String> caseSnippets,
            Map<String, List<String>> tagValues,
            Integer itemSeq,
            Integer batchSize,
            OpeningStrategy openingStrategy
    ) {
        Long userId = param.getUserId() != null ? param.getUserId() : WebSessionHolder.getUserId();
        return ScriptGenerationPromptContext.builder()
                .productId(param.getProductId())
                .userId(userId)
                .productName(product.getProductName())
                .itemSeq(itemSeq)
                .batchSize(batchSize)
                .systemPrompt(prompt.getSystemPrompt())
                .ruleSnapshot(ruleSnapshot)
                .caseSnippets(caseSnippets)
                .tagValues(tagValues)
                .controlParams(param.getControlParams())
                .openingStrategy(openingStrategy)
                .build();
    }

    private ScriptGenerationLlmResult generateScript(ScriptGenerationPrompt promptContent) {
        LlmChatResponse response = llmService.chatCompletion(
                AiSceneType.SCRIPT_GENERATION,
                promptContent.getSystemPrompt(),
                promptContent.getUserContent()
        );
        return scriptGenerationLlmResponseParser.parse(response);
    }

    @lombok.Getter
    @RequiredArgsConstructor
    private static class GenerateItemResult {

        private final Integer itemSeq;
        private final ScriptGenerateVO result;
        private final String errorMsg;

        private static GenerateItemResult success(Integer itemSeq, ScriptGenerateVO result) {
            return new GenerateItemResult(itemSeq, result, null);
        }

        private static GenerateItemResult fail(Integer itemSeq, String errorMsg) {
            return new GenerateItemResult(itemSeq, null, errorMsg);
        }

        private boolean isSuccess() {
            return result != null;
        }
    }
}
