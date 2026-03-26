package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.service.llm.LlmChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 脚本生成模型结果解析器
 *
 * @author codex
 * @since 2026-03-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScriptGenerationLlmResponseParser {

    private final ObjectMapper objectMapper;

    public ScriptGenerationLlmResult parse(LlmChatResponse response) {
        String rawContent = response.getRawContent();
        String cleanedContent = cleanRawContent(rawContent);
        if (cleanedContent == null || cleanedContent.isBlank()) {
            log.error("脚本生成返回内容为空，provider={}, model={}, rawContent={}",
                    response.getProvider(), response.getModel(), rawContent);
            throw new BusinessException("脚本生成失败，大模型返回内容为空");
        }

        try {
            JsonNode resultNode = objectMapper.readTree(cleanedContent);
            String script = readRequiredText(resultNode, "script", response);
            return ScriptGenerationLlmResult.builder()
                    .title(readText(resultNode, "title"))
                    .script(script)
                    .summary(readText(resultNode, "summary"))
                    .scene(readText(resultNode, "scene"))
                    .targetUser(readText(resultNode, "target_user"))
                    .emotion(readText(resultNode, "emotion"))
                    .tone(readText(resultNode, "tone"))
                    .sellingPoints(readTextList(resultNode, "selling_points"))
                    .structure(readStructure(resultNode.path("structure")))
                    .tags(readTextList(resultNode, "tags"))
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("脚本生成返回 JSON 解析失败，provider={}, model={}, rawContent={}",
                    response.getProvider(), response.getModel(), rawContent, e);
            throw new BusinessException("脚本生成失败，大模型返回格式非法");
        }
    }

    private String cleanRawContent(String rawContent) {
        if (rawContent == null) {
            return null;
        }
        String cleaned = rawContent.trim();
        cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
        cleaned = cleaned.replaceFirst("\\s*```$", "");
        return cleaned.trim();
    }

    private String readRequiredText(JsonNode node, String fieldName, LlmChatResponse response) {
        String value = readText(node, fieldName);
        if (value == null || value.isBlank()) {
            log.error("脚本生成返回缺少必填字段，field={}, provider={}, model={}, rawContent={}",
                    fieldName, response.getProvider(), response.getModel(), response.getRawContent());
            throw new BusinessException("脚本生成失败，大模型返回结果缺少脚本内容");
        }
        return value;
    }

    private String readText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        String value = fieldNode.asText(null);
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> readTextList(JsonNode node, String fieldName) {
        JsonNode arrayNode = node.path(fieldName);
        if (!arrayNode.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (JsonNode itemNode : arrayNode) {
            String item = itemNode.asText(null);
            if (item != null && !item.isBlank()) {
                values.add(item.trim());
            }
        }
        return values;
    }

    private ScriptStructure readStructure(JsonNode structureNode) {
        if (structureNode == null || !structureNode.isObject()) {
            return ScriptStructure.builder().build();
        }
        return ScriptStructure.builder()
                .hook(readText(structureNode, "hook"))
                .problem(readText(structureNode, "problem"))
                .solution(readText(structureNode, "solution"))
                .cta(readText(structureNode, "cta"))
                .build();
    }
}
