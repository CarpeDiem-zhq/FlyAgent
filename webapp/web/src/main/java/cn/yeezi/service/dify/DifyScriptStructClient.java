package cn.yeezi.service.dify;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.model.param.ExcellentScriptStructAddParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Dify 优秀脚本结构客户端
 *
 * @author codex
 * @since 2026-03-02
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class DifyScriptStructClient {

    @Value("${dify.base-url}")
    private String baseUrl;

    @Value("${dify.qwen.workflow-path}")
    private String workflowPath;

    @Value("${dify.script-struct.api-key:}")
    private String scriptStructApiKey;

    @Value("${dify.knowledge.api-key:}")
    private String knowledgeApiKey;

    @Value("${dify.knowledge.dataset-id:}")
    private String datasetId;

    @Value("${dify.knowledge.document-id:}")
    private String documentId;

    @Value("${dify.knowledge.segment-path-template:/v1/datasets/%s/documents/%s/segments}")
    private String segmentPathTemplate;

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public JsonNode runScriptStructWorkflow(ExcellentScriptStructAddParam param, String userPhone) {
        validateScriptStructConfig();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode inputs = mapper.createObjectNode();
        inputs.put("excellent_script", param.getExcellentScript());
        inputs.put("product_name", param.getProductName());
        inputs.put("function_name", param.getFunctionName());
        root.set("inputs", inputs);
        root.put("response_mode", "blocking");
        root.put("user", userPhone);
        String jsonBody = root.toString();
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + workflowPath)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + scriptStructApiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Dify 优秀脚本结构拆解请求失败 code={}, message={}", response.code(), response.message());
                throw new BusinessException("网络请求异常，请稍后重试");
            }
            if (response.body() == null) {
                throw new BusinessException("网络请求异常，请稍后重试");
            }
            String responseBodyString = response.body().string();
            JsonNode rootNode = mapper.readTree(responseBodyString);
            // 取 data.outputs.text
            String text = rootNode
                    .path("data")
                    .path("outputs")
                    .path("text")
                    .asText();
            log.info("Dify 优秀脚本结构拆解 text内容: {}", text);
            return rootNode;
        } catch (IOException e) {
            log.error("Dify 优秀脚本结构拆解网络异常", e);
            throw new BusinessException("网络请求异常，请稍后重试");
        }
    }

    public JsonNode addKnowledgeSegment(String structuredText, String productName, String functionName) {
        validateKnowledgeConfig();
        String path = String.format(segmentPathTemplate, datasetId, documentId);
        ObjectNode root = mapper.createObjectNode();
        ArrayNode segments = mapper.createArrayNode();
        ObjectNode segment = mapper.createObjectNode();
        segment.put("content", structuredText);
        segment.put("answer", "");
        ArrayNode keywords = mapper.createArrayNode();
        keywords.add(productName == null ? "" : productName);
        keywords.add(functionName == null ? "" : functionName);
        segment.set("keywords", keywords);
        segments.add(segment);
        root.set("segments", segments);
        String jsonBody = root.toString();
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + knowledgeApiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Dify 知识库新增块请求失败 code={}, message={}", response.code(), response.message());
                throw new BusinessException("写入知识库失败，请稍后重试");
            }
            if (response.body() == null) {
                throw new BusinessException("写入知识库失败，请稍后重试");
            }
            String responseBodyString = response.body().string();
            JsonNode rootNode = mapper.readTree(responseBodyString);
            log.info("Dify 知识库新增块响应: {}", rootNode);
            return rootNode;
        } catch (IOException e) {
            log.error("Dify 知识库新增块网络异常", e);
            throw new BusinessException("网络请求异常，请稍后重试");
        }
    }

    public String getDatasetId() {
        return datasetId;
    }

    public String getDocumentId() {
        return documentId;
    }

    private void validateScriptStructConfig() {
        if (scriptStructApiKey == null || scriptStructApiKey.isBlank()) {
            throw new BusinessException("优秀脚本结构拆解配置缺失");
        }
    }

    private void validateKnowledgeConfig() {
        if (knowledgeApiKey == null || knowledgeApiKey.isBlank()
                || datasetId == null || datasetId.isBlank()
                || documentId == null || documentId.isBlank()) {
            throw new BusinessException("知识库配置缺失");
        }
    }
}
