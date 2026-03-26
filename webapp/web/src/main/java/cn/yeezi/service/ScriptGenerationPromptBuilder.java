package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.model.param.ControlParamsParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 脚本生成提示构造器
 *
 * @author codex
 * @since 2026-03-12
 */
@Component
@RequiredArgsConstructor
public class ScriptGenerationPromptBuilder {

    private final ObjectMapper objectMapper;

    public ScriptGenerationPrompt buildGeneratePrompt(ScriptGenerationPromptContext context) {
        Map<String, Object> userPayload = buildBaseUserPayload(context);
        userPayload.put("taskType", "GENERATE");
        userPayload.put("taskInstruction", "请根据业务数据生成广告脚本，并严格按约定 JSON 返回。");
        return buildPrompt(context.getSystemPrompt(), context, userPayload);
    }

    public ScriptGenerationPrompt buildRerunPrompt(
            ScriptGenerationPromptContext context,
            String basePromptSnapshot,
            String inputSnapshot,
            String outputContent,
            String instruction
    ) {
        Map<String, Object> userPayload = buildBaseUserPayload(context);
        userPayload.put("taskType", "RERUN");
        userPayload.put("basePromptSnapshot", basePromptSnapshot);
        userPayload.put("currentScript", outputContent);
        userPayload.put("originalInputSnapshot", inputSnapshot);
        userPayload.put("additionalInstruction", instruction);
        userPayload.put("taskInstruction", "请基于当前脚本和修改意见，重写脚本并保持输出为约定 JSON。");
        return buildPrompt(context.getSystemPrompt(), context, userPayload);
    }

    public ScriptGenerationPrompt buildFeedbackRerunPrompt(
            ScriptGenerationPromptContext context,
            String basePromptSnapshot,
            String inputSnapshot,
            String outputContent,
            List<String> reasonCodes,
            String suggestion
    ) {
        Map<String, Object> userPayload = buildBaseUserPayload(context);
        userPayload.put("taskType", "FEEDBACK_RERUN");
        userPayload.put("basePromptSnapshot", basePromptSnapshot);
        userPayload.put("currentScript", outputContent);
        userPayload.put("originalInputSnapshot", inputSnapshot);
        userPayload.put("feedbackReasonCodes", reasonCodes);
        userPayload.put("feedbackSuggestion", suggestion);
        userPayload.put("taskInstruction", "请结合反馈原因和修改建议，重写脚本并保持输出为约定 JSON。");
        return buildPrompt(context.getSystemPrompt(), context, userPayload);
    }

    private ScriptGenerationPrompt buildPrompt(String customSystemPrompt, ScriptGenerationPromptContext context, Map<String, Object> userPayload) {
        String systemPrompt = buildSystemPrompt(customSystemPrompt, context);
        String userContent = writeJson(userPayload);
        String promptSnapshot = "【system】\n" + systemPrompt + "\n\n【user】\n" + userContent;
        return ScriptGenerationPrompt.builder()
                .systemPrompt(systemPrompt)
                .userContent(userContent)
                .promptSnapshot(promptSnapshot)
                .build();
    }

    private Map<String, Object> buildBaseUserPayload(ScriptGenerationPromptContext context) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("product", buildProductPayload(context));
        payload.put("batchContext", buildBatchContext(context));
        payload.put("tagValues", context.getTagValues());
        payload.put("controlParams", buildControlPayload(context.getControlParams()));
        payload.put("openingStrategy", buildOpeningStrategyPayload(context.getOpeningStrategy()));
        payload.put("ruleSnapshot", context.getRuleSnapshot());
        payload.put("caseSnippets", context.getCaseSnippets());
        return payload;
    }

    private Map<String, Object> buildProductPayload(ScriptGenerationPromptContext context) {
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("productId", context.getProductId());
        product.put("productName", context.getProductName());
        product.put("userId", context.getUserId());
        return product;
    }

    private Map<String, Object> buildBatchContext(ScriptGenerationPromptContext context) {
        Map<String, Object> batchContext = new LinkedHashMap<>();
        batchContext.put("itemSeq", context.getItemSeq());
        batchContext.put("batchSize", context.getBatchSize());
        return batchContext;
    }

    private Map<String, Object> buildControlPayload(ControlParamsParam controlParams) {
        Map<String, Object> controlPayload = new LinkedHashMap<>();
        if (controlParams == null) {
            return controlPayload;
        }
        controlPayload.put("ad_words", controlParams.getAd_words());
        controlPayload.put("key_scenes", controlParams.getKey_scenes());
        return controlPayload;
    }

    private Map<String, Object> buildOpeningStrategyPayload(OpeningStrategy openingStrategy) {
        Map<String, Object> strategyPayload = new LinkedHashMap<>();
        if (openingStrategy == null) {
            return strategyPayload;
        }
        strategyPayload.put("code", openingStrategy.code());
        strategyPayload.put("name", openingStrategy.name());
        strategyPayload.put("instruction", openingStrategy.instruction());
        return strategyPayload;
    }

    private String buildSystemPrompt(String customSystemPrompt, ScriptGenerationPromptContext context) {
        StringBuilder builder = new StringBuilder();
        if (customSystemPrompt != null && !customSystemPrompt.isBlank()) {
            builder.append(customSystemPrompt.trim()).append("\n\n");
        }
        builder.append("你是广告脚本生成助手，需要根据用户提供的结构化业务数据生成广告脚本。\n");
        appendAdWordsConstraint(builder, context.getControlParams());
        appendOpeningStrategyConstraint(builder, context);
        builder.append("你必须只返回合法 JSON，不要返回 markdown、代码块、解释说明或其它额外文本。\n");
        builder.append("返回 JSON 至少包含以下字段：title、script、summary、scene、target_user、emotion、tone、selling_points、structure、tags。\n");
        builder.append("其中 structure 必须是对象，至少包含 hook、problem、solution、cta；selling_points 与 tags 必须是数组。\n");
        builder.append("script 字段必须是非空字符串，并输出完整广告脚本正文。\n");
        builder.append("允许在 JSON 中增加扩展字段，但必须保证上述字段存在且类型正确。");
        return builder.toString().trim();
    }

    private void appendAdWordsConstraint(StringBuilder builder, ControlParamsParam controlParams) {
        if (controlParams == null) {
            return;
        }
        String adWords = controlParams.getAd_words();
        if (adWords == null || adWords.isBlank()) {
            return;
        }
        builder.append("脚本正文的字数必须严格遵守以下限制：")
                .append(adWords.trim())
                .append("。该限制仅针对返回 JSON 中的 script 正文内容。\n");
    }

    private void appendOpeningStrategyConstraint(StringBuilder builder, ScriptGenerationPromptContext context) {
        if (context == null || context.getOpeningStrategy() == null) {
            return;
        }
        OpeningStrategy openingStrategy = context.getOpeningStrategy();
        if (context.getItemSeq() != null && context.getBatchSize() != null) {
            builder.append("当前为批量生成任务中的第")
                    .append(context.getItemSeq())
                    .append("条，共")
                    .append(context.getBatchSize())
                    .append("条。\n");
        }
        builder.append("本条脚本开头策略为：")
                .append(openingStrategy.name())
                .append("。\n");
        builder.append("策略说明：")
                .append(openingStrategy.instruction())
                .append("。\n");
        builder.append("要求开头必须体现该策略特征，前两句完成该策略切入，并尽量避免与同批次其它脚本使用相似句式。\n");
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException("脚本生成提示构造失败");
        }
    }
}
