package cn.yeezi.service;

import cn.yeezi.ai.AiSceneType;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.entity.PromptEntity;
import cn.yeezi.db.entity.ScriptAssetEntity;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.repository.ScriptAssetRepository;
import cn.yeezi.model.param.ScriptGenerateParam;
import cn.yeezi.model.vo.ScriptGenerateVO;
import cn.yeezi.model.vo.ScriptQualityCheckVO;
import cn.yeezi.service.llm.LlmChatResponse;
import cn.yeezi.service.llm.LlmService;
import cn.yeezi.web.WebSessionHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScriptGenerationService {

    private final ProductService productService;
    private final PromptService promptService;
    private final ProductFeatureService productFeatureService;
    private final FeatureSellingPointService featureSellingPointService;
    private final StrategyService strategyService;
    private final LlmService llmService;
    private final ScriptGenerationLlmResponseParser scriptGenerationLlmResponseParser;
    private final ScriptQualityCheckService scriptQualityCheckService;
    private final ScriptAssetRepository scriptAssetRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ScriptGenerateVO generate(ScriptGenerateParam param) {
        Long userId = WebSessionHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        return generate(
                param.getProductId(),
                param.getFeatureId(),
                param.getCoreSellingPointIds(),
                param.getStrategyId(),
                userId
        );
    }

    @Transactional
    public ScriptGenerateVO generate(
            Long productId,
            Long featureId,
            Long coreSellingPointId,
            Long strategyId,
            Long userId
    ) {
        return generate(productId, featureId, Collections.singletonList(coreSellingPointId), strategyId, userId);
    }

    @Transactional
    public ScriptGenerateVO generate(
            Long productId,
            Long featureId,
            List<Long> coreSellingPointIds,
            Long strategyId,
            Long userId
    ) {
        ProductEntity product = productService.requireActiveProduct(productId);
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(featureId);
        List<FeatureSellingPointEntity> sellingPoints = loadSellingPoints(coreSellingPointIds);
        StrategyEntity strategy = strategyService.requireActiveStrategy(strategyId);
        validateRelation(product, feature, sellingPoints, strategy);

        PromptEntity prompt = promptService.requireActivePromptByProduct(productId);
        String systemPrompt = buildSystemPrompt(prompt.getSystemPrompt());
        String userContent = buildUserContent(product, feature, sellingPoints, strategy);
        LlmChatResponse response = llmService.chatCompletion(AiSceneType.SCRIPT_GENERATION, systemPrompt, userContent);
        ScriptGenerationLlmResult llmResult = scriptGenerationLlmResponseParser.parse(response);
        ScriptQualityCheckVO qualityCheck = scriptQualityCheckService.check(llmResult, strategy.getAdWords());

        List<Long> normalizedSellingPointIds = normalizeIds(coreSellingPointIds);
        List<String> sellingPointNames = sellingPoints.stream()
                .map(FeatureSellingPointEntity::getSellingPointName)
                .collect(Collectors.toList());

        ScriptAssetEntity asset = new ScriptAssetEntity()
                .setProductId(product.getId())
                .setFeatureId(feature.getId())
                .setCoreSellingPointId(normalizedSellingPointIds.isEmpty() ? null : normalizedSellingPointIds.get(0))
                .setCoreSellingPointIds(writeJson(normalizedSellingPointIds))
                .setStrategyId(strategy.getId())
                .setUserId(userId)
                .setPromptId(prompt.getId())
                .setSystemPromptSnapshot(systemPrompt)
                .setStrategySnapshot(writeJson(buildStrategySnapshot(strategy, normalizedSellingPointIds, sellingPointNames)))
                .setFeatureSnapshot(writeJson(buildFeatureSnapshot(feature)))
                .setSellingPointSnapshot(writeJson(buildSellingPointSnapshot(sellingPoints)))
                .setScriptTitle(defaultIfBlank(llmResult.getTitle(), product.getProductName() + "脚本"))
                .setScriptContent(llmResult.getScript())
                .setModelName(llmService.getCurrentModel(AiSceneType.SCRIPT_GENERATION))
                .setDel(false);
        scriptAssetRepository.save(asset);

        ScriptGenerateVO vo = new ScriptGenerateVO();
        vo.setAssetId(asset.getId());
        vo.setProductId(product.getId());
        vo.setFeatureId(feature.getId());
        vo.setCoreSellingPointIds(normalizedSellingPointIds);
        vo.setCoreSellingPointNames(sellingPointNames);
        vo.setStrategyId(strategy.getId());
        vo.setPromptId(prompt.getId());
        vo.setScriptTitle(asset.getScriptTitle());
        vo.setScriptContent(asset.getScriptContent());
        vo.setModelName(asset.getModelName());
        vo.setQualityCheck(qualityCheck);
        return vo;
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

    private List<FeatureSellingPointEntity> loadSellingPoints(List<Long> coreSellingPointIds) {
        List<Long> normalizedIds = normalizeIds(coreSellingPointIds);
        if (normalizedIds.isEmpty()) {
            throw new BusinessException("至少选择一个核心卖点");
        }
        return normalizedIds.stream()
                .map(featureSellingPointService::requireActiveSellingPoint)
                .collect(Collectors.toList());
    }

    private void validateRelation(
            ProductEntity product,
            ProductFeatureEntity feature,
            List<FeatureSellingPointEntity> sellingPoints,
            StrategyEntity strategy
    ) {
        if (!product.getId().equals(feature.getProductId())) {
            throw new BusinessException("产品功能不属于该产品");
        }
        for (FeatureSellingPointEntity sellingPoint : sellingPoints) {
            if (!product.getId().equals(sellingPoint.getProductId())
                    || !feature.getId().equals(sellingPoint.getFeatureId())) {
                throw new BusinessException("核心卖点不属于当前产品功能");
            }
        }
        if (!product.getId().equals(strategy.getProductId())
                || !feature.getId().equals(strategy.getFeatureId())) {
            throw new BusinessException("策略不属于当前产品功能");
        }
        List<Long> strategySellingPointIds = strategyService.listActiveSellingPointIds(strategy.getId());
        List<Long> normalizedIds = normalizeIds(sellingPoints.stream()
                .map(FeatureSellingPointEntity::getId)
                .collect(Collectors.toList()));
        if (!strategySellingPointIds.equals(normalizedIds)) {
            throw new BusinessException("策略与核心卖点组合不匹配");
        }
    }

    private String buildSystemPrompt(String productPrompt) {
        StringBuilder builder = new StringBuilder();
        builder.append(defaultIfBlank(productPrompt, "你是专业的短视频广告脚本策划。")).append("\n")
                .append("请严格输出 JSON，不要输出 markdown，不要输出代码块。\n")
                .append("JSON 字段固定为：title、summary、scene、target_user、emotion、tone、selling_points、structure、tags、script。\n")
                .append("structure 字段固定包含：hook、problem、solution、cta。");
        return builder.toString();
    }

    private String buildUserContent(
            ProductEntity product,
            ProductFeatureEntity feature,
            List<FeatureSellingPointEntity> sellingPoints,
            StrategyEntity strategy
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("product_name", product.getProductName());
        payload.put("product_desc", product.getProductDesc());
        payload.put("feature_name", feature.getFeatureName());
        payload.put("core_selling_points", sellingPoints.stream()
                .map(FeatureSellingPointEntity::getSellingPointName)
                .collect(Collectors.toList()));
        payload.put("strategy_name", strategy.getStrategyName());
        payload.put("target_audience", strategy.getTargetAudience());
        payload.put("target_scene", strategy.getTargetScene());
        payload.put("tone_style", strategy.getToneStyle());
        payload.put("call_to_action", strategy.getCallToAction());
        payload.put("ad_words", strategy.getAdWords());
        payload.put("requirements", List.of(
                "基于以上产品信息生成一条广告脚本",
                "脚本内容要突出全部核心卖点和目标人群",
                "标题和正文要便于直接用于投放或口播"
        ));
        return writeJson(payload);
    }

    private Map<String, Object> buildStrategySnapshot(
            StrategyEntity strategy,
            List<Long> coreSellingPointIds,
            List<String> coreSellingPointNames
    ) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", strategy.getId());
        snapshot.put("strategy_name", strategy.getStrategyName());
        snapshot.put("feature_id", strategy.getFeatureId());
        snapshot.put("feature_name", strategy.getFeatureName());
        snapshot.put("core_selling_point_ids", coreSellingPointIds);
        snapshot.put("core_selling_point_names", coreSellingPointNames);
        snapshot.put("target_audience", strategy.getTargetAudience());
        snapshot.put("target_scene", strategy.getTargetScene());
        snapshot.put("tone_style", strategy.getToneStyle());
        snapshot.put("call_to_action", strategy.getCallToAction());
        snapshot.put("ad_words", strategy.getAdWords());
        return snapshot;
    }

    private Map<String, Object> buildFeatureSnapshot(ProductFeatureEntity feature) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", feature.getId());
        snapshot.put("feature_name", feature.getFeatureName());
        return snapshot;
    }

    private List<Map<String, Object>> buildSellingPointSnapshot(List<FeatureSellingPointEntity> sellingPoints) {
        return sellingPoints.stream().map(sellingPoint -> {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("id", sellingPoint.getId());
            snapshot.put("selling_point_name", sellingPoint.getSellingPointName());
            return snapshot;
        }).collect(Collectors.toList());
    }

    private List<Long> normalizeIds(List<Long> ids) {
        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
