package cn.yeezi.service.dify;

import cn.dev33.satoken.stp.StpUtil;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.model.param.DifyChatAssistantParam;
import cn.yeezi.web.WebSessionHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Log4j2
@Component
@RequiredArgsConstructor
public class DifyChatAssistantClient {
    @Value("${dify.base-url}")
    private String baseUrl;

    @Value("${dify.chat.path}")
    private String chatPath;

    @Value("${dify.chat.api-key}")
    private String apiKey;

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public JsonNode chatAssistant(DifyChatAssistantParam params) {
        // 组装 inputs 参数
        String jsonBody = mapper.createObjectNode()
                .putPOJO("inputs", params.getInputs())
                .put("query", params.getQuery())
                .put("conversation_id", params.getConversation_id())
                .put("response_mode", "blocking")
                .put("user", StpUtil.isLogin() ? WebSessionHolder.getUserPhone() : "user-guest")
                .toString();
        log.info("Dify_chatAssistant Request {}", jsonBody);
        RequestBody requestBody = RequestBody.create(
                jsonBody, MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + chatPath)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            log.info("Dify_chatAssistant Response {}", response);
            if (!response.isSuccessful()) {
                log.error("Dify Chat 响应异常: {}", response.message());
                throw new RuntimeException("网络请求异常，请稍后重试: " + response.code() + " " + response.message());
            }

            if (response.body() != null) {
                try {
                    return mapper.readTree(response.body().string());
                } catch (IOException e) {
                    log.error("解析 Dify 响应失败", e);
                    throw new RuntimeException("网络请求异常，请稍后重试", e);
                }
            }
        } catch (Exception e) {
            log.error("Dify Chat 请求异常", e);
            throw new BusinessException("网络请求异常，请稍后重试");
        }
        return null;
    }

}