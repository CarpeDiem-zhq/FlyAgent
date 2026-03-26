package cn.yeezi.service;

import cn.yeezi.ai.AiSceneType;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.entity.PromptEntity;
import cn.yeezi.db.entity.ScriptGenerateBatchEntity;
import cn.yeezi.db.entity.ScriptGenerateHistoryEntity;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.repository.ProductRepository;
import cn.yeezi.db.repository.ScriptGenerateBatchRepository;
import cn.yeezi.db.repository.ScriptGenerateHistoryRepository;
import cn.yeezi.model.dto.OpenClawScriptDraftDTO;
import cn.yeezi.model.param.OpenClawScriptGenerateParam;
import cn.yeezi.model.param.OpenClawScriptResolveParam;
import cn.yeezi.model.vo.OpenClawOptionVO;
import cn.yeezi.model.vo.OpenClawScriptGenerateVO;
import cn.yeezi.model.vo.OpenClawScriptResolveVO;
import cn.yeezi.model.vo.ScriptQualityCheckVO;
import cn.yeezi.service.llm.LlmChatResponse;
import cn.yeezi.service.llm.LlmService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenClawScriptFacadeService {

    private static final String STATUS_ASK = "ASK";
    private static final String STATUS_READY = "READY";
    private static final String STATUS_UNSUPPORTED = "UNSUPPORTED";

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductFeatureService productFeatureService;
    private final FeatureSellingPointService featureSellingPointService;
    private final StrategyService strategyService;
    private final PromptService promptService;
    private final LlmService llmService;
    private final ScriptGenerationLlmResponseParser parser;
    private final ScriptQualityCheckService qualityCheckService;
    private final ScriptGenerateBatchRepository batchRepository;
    private final ScriptGenerateHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    public OpenClawScriptResolveVO resolve(OpenClawScriptResolveParam param) {
        OpenClawScriptDraftDTO draft = param.getCurrentDraft() == null ? new OpenClawScriptDraftDTO() : param.getCurrentDraft();
        String traceId = draft.getTraceId() == null || draft.getTraceId().isBlank()
                ? UUID.randomUUID().toString()
                : draft.getTraceId();
        String message = param.getMessageText().trim();

        if (!looksLikeScriptIntent(message) && draft.getProductId() == null) {
            return OpenClawScriptResolveVO.builder()
                    .status(STATUS_UNSUPPORTED)
                    .currentStep("UNSUPPORTED")
                    .traceId(traceId)
                    .askMessage("")
                    .build();
        }

        if (draft.getProductId() == null) {
            Long productId = matchProductId(message);
            if (productId == null) {
                return buildAsk(traceId, "PRODUCT", "请先选择产品", draft, listProducts(), null, null, null);
            }
            draft.setProductId(productId);
        }

        if (draft.getFeatureId() == null) {
            List<OpenClawOptionVO> options = listFeatureOptions(draft.getProductId());
            Long featureId = matchOptionId(message, options);
            if (featureId == null) {
                return buildAsk(traceId, "FEATURE", "请选择产品功能", draft, null, options, null, null);
            }
            draft.setFeatureId(featureId);
        }

        if (draft.getCoreSellingPointId() == null) {
            List<OpenClawOptionVO> options = listSellingPointOptions(draft.getProductId(), draft.getFeatureId());
            Long sellingPointId = matchOptionId(message, options);
            if (sellingPointId == null) {
                return buildAsk(traceId, "SELLING_POINT", "请选择核心卖点", draft, null, null, options, null);
            }
            draft.setCoreSellingPointId(sellingPointId);
        }

        if (draft.getStrategyId() == null) {
            List<OpenClawOptionVO> options = listStrategyOptions(
                    draft.getProductId(), draft.getFeatureId(), draft.getCoreSellingPointId());
            Long strategyId = matchOptionId(message, options);
            if (strategyId == null) {
                return buildAsk(traceId, "STRATEGY", "请选择策略", draft, null, null, null, options);
            }
            draft.setStrategyId(strategyId);
        }

        return OpenClawScriptResolveVO.builder()
                .status(STATUS_READY)
                .currentStep("READY")
                .traceId(traceId)
                .productId(draft.getProductId())
                .featureId(draft.getFeatureId())
                .coreSellingPointId(draft.getCoreSellingPointId())
                .strategyId(draft.getStrategyId())
                .askMessage("参数已齐全，开始生成")
                .build();
    }

    @Transactional
    public OpenClawScriptGenerateVO generate(OpenClawScriptGenerateParam param) {
        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(param.getFeatureId());
        FeatureSellingPointEntity sellingPoint =
                featureSellingPointService.requireActiveSellingPoint(param.getCoreSellingPointId());
        StrategyEntity strategy = strategyService.requireActiveStrategy(param.getStrategyId());
        if (!product.getId().equals(feature.getProductId())
                || !product.getId().equals(sellingPoint.getProductId())
                || !feature.getId().equals(sellingPoint.getFeatureId())
                || !product.getId().equals(strategy.getProductId())
                || !feature.getId().equals(strategy.getFeatureId())
                || !sellingPoint.getId().equals(strategy.getCoreSellingPointId())) {
            throw new BusinessException("生成参数关系不一致");
        }

        PromptEntity prompt = strategy.getPromptId() == null
                ? promptService.getActivePrompt(product.getId(), null)
                : promptService.getActivePrompt(product.getId(), strategy.getPromptId());
        String systemPrompt = buildSystemPrompt(prompt.getSystemPrompt(), strategy.getAdWords());
        String userContent = buildUserContent(product, feature, sellingPoint, strategy);
        LlmChatResponse response = llmService.chatCompletion(AiSceneType.SCRIPT_GENERATION, systemPrompt, userContent);
        ScriptGenerationLlmResult result = parser.parse(response);
        ScriptQualityCheckVO qualityCheck = qualityCheckService.check(result, strategy.getAdWords());

        ScriptGenerateBatchEntity batch = new ScriptGenerateBatchEntity()
                .setProductId(product.getId())
                .setUserId(param.getUserId())
                .setPromptId(prompt.getId())
                .setAdNumber(param.getAdNumber() == null ? 1 : param.getAdNumber())
                .setSourceType("OPENCLAW")
                .setSuccessCount(1)
                .setFailCount(0)
                .setStatus(1)
                .setRequestSnapshot(writeJson(param))
                .setDel(false);
        batchRepository.save(batch);

        ScriptGenerateHistoryEntity history = new ScriptGenerateHistoryEntity()
                .setBatchId(batch.getId())
                .setItemSeq(1)
                .setProductId(product.getId())
                .setUserId(param.getUserId())
                .setPromptId(prompt.getId())
                .setPromptSnapshot(systemPrompt)
                .setRuleSnapshot(writeJson(strategy))
                .setInputSnapshot(writeJson(param))
                .setUserInputSnapshot(userContent)
                .setModelName(llmService.getCurrentModel(AiSceneType.SCRIPT_GENERATION))
                .setRouteStrategy("OPENCLAW_STRATEGY")
                .setOutputContent(result.getScript())
                .setSourceType("OPENCLAW")
                .setRevisionSeq(0)
                .setSaveStatus(false)
                .setDel(false);
        historyRepository.save(history);

        return OpenClawScriptGenerateVO.builder()
                .traceId(param.getTraceId())
                .batchId(batch.getId())
                .historyId(history.getId())
                .productId(product.getId())
                .featureId(feature.getId())
                .coreSellingPointId(sellingPoint.getId())
                .strategyId(strategy.getId())
                .displayText(buildDisplayText(product, feature, sellingPoint, strategy, result, qualityCheck))
                .outputContent(result.getScript())
                .qualityCheck(qualityCheck)
                .build();
    }

    private OpenClawScriptResolveVO buildAsk(
            String traceId,
            String step,
            String askMessage,
            OpenClawScriptDraftDTO draft,
            List<OpenClawOptionVO> productOptions,
            List<OpenClawOptionVO> featureOptions,
            List<OpenClawOptionVO> sellingPointOptions,
            List<OpenClawOptionVO> strategyOptions
    ) {
        return OpenClawScriptResolveVO.builder()
                .status(STATUS_ASK)
                .currentStep(step)
                .traceId(traceId)
                .productId(draft.getProductId())
                .featureId(draft.getFeatureId())
                .coreSellingPointId(draft.getCoreSellingPointId())
                .strategyId(draft.getStrategyId())
                .askMessage(askMessage)
                .productOptions(productOptions)
                .featureOptions(featureOptions)
                .sellingPointOptions(sellingPointOptions)
                .strategyOptions(strategyOptions)
                .build();
    }

    private boolean looksLikeScriptIntent(String message) {
        return message.contains("脚本") || message.contains("广告") || message.contains("生成");
    }

    private Long matchProductId(String message) {
        LambdaQueryWrapper<ProductEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductEntity::getDel, false);
        List<ProductEntity> matches = productRepository.list(query).stream()
                .filter(item -> item.getProductName() != null && message.contains(item.getProductName()))
                .collect(Collectors.toList());
        return matches.size() == 1 ? matches.get(0).getId() : null;
    }

    private Long matchOptionId(String message, List<OpenClawOptionVO> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        String normalized = message.trim();
        try {
            long numeric = Long.parseLong(normalized);
            return options.stream()
                    .filter(option -> option.getId() != null && option.getId() == numeric)
                    .map(OpenClawOptionVO::getId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException ignored) {
        }
        List<OpenClawOptionVO> matches = options.stream()
                .filter(option -> option.getLabel() != null && normalized.contains(option.getLabel()))
                .collect(Collectors.toList());
        return matches.size() == 1 ? matches.get(0).getId() : null;
    }

    private List<OpenClawOptionVO> listProducts() {
        LambdaQueryWrapper<ProductEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductEntity::getDel, false);
        query.orderByAsc(ProductEntity::getId);
        return productRepository.list(query).stream()
                .map(item -> new OpenClawOptionVO(item.getId(), item.getProductName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listFeatureOptions(Long productId) {
        return productFeatureService.listByProduct(productId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getFeatureName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listSellingPointOptions(Long productId, Long featureId) {
        return featureSellingPointService.listByFeature(productId, featureId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getSellingPointName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listStrategyOptions(Long productId, Long featureId, Long coreSellingPointId) {
        return strategyService.list(productId, featureId, coreSellingPointId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getStrategyName()))
                .collect(Collectors.toList());
    }

    private String buildSystemPrompt(String basePrompt, String adWords) {
        StringBuilder builder = new StringBuilder();
        if (basePrompt != null && !basePrompt.isBlank()) {
            builder.append(basePrompt.trim()).append("\n\n");
        }
        builder.append("你是广告脚本生成助手，必须返回合法 JSON。");
        builder.append("\n返回字段至少包含：title、script、summary、scene、target_user、emotion、tone、selling_points、structure、tags。");
        builder.append("\nstructure 至少包含 hook、problem、solution、cta。");
        if (adWords != null && !adWords.isBlank()) {
            builder.append("\nscript 字段的字数限制为：").append(adWords.trim()).append("。");
        }
        return builder.toString();
    }

    private String buildUserContent(
            ProductEntity product,
            ProductFeatureEntity feature,
            FeatureSellingPointEntity sellingPoint,
            StrategyEntity strategy
    ) {
        return writeJson(new UserPayload(
                product.getId(), product.getProductName(),
                feature.getId(), feature.getFeatureName(),
                sellingPoint.getId(), sellingPoint.getSellingPointName(),
                strategy.getId(), strategy.getStrategyName(),
                strategy.getTargetAudience(), strategy.getTargetScene(),
                strategy.getToneStyle(), strategy.getCallToAction(), strategy.getAdWords()));
    }

    private String buildDisplayText(
            ProductEntity product,
            ProductFeatureEntity feature,
            FeatureSellingPointEntity sellingPoint,
            StrategyEntity strategy,
            ScriptGenerationLlmResult result,
            ScriptQualityCheckVO qualityCheck
    ) {
        String qualityText = Boolean.TRUE.equals(qualityCheck.getPassed()) ? "通过" : "未通过";
        return "产品：" + product.getProductName()
                + "\n功能：" + feature.getFeatureName()
                + "\n卖点：" + sellingPoint.getSellingPointName()
                + "\n策略：" + strategy.getStrategyName()
                + "\n质检：" + qualityText
                + "\n\n" + result.getScript();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException("JSON 序列化失败");
        }
    }

    private record UserPayload(
            Long productId,
            String productName,
            Long featureId,
            String featureName,
            Long coreSellingPointId,
            String coreSellingPointName,
            Long strategyId,
            String strategyName,
            String targetAudience,
            String targetScene,
            String toneStyle,
            String callToAction,
            String adWords
    ) {
    }
}
