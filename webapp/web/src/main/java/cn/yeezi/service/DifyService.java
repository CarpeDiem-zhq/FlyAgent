package cn.yeezi.service;

import cn.yeezi.model.param.DifyChatAssistantParam;
import cn.yeezi.model.vo.DifyChatAssistantVO;
import cn.yeezi.service.dify.DifyChatAssistantClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DifyService {
    private final DifyChatAssistantClient difyChatAssistantClient;

    public DifyChatAssistantVO chatAssistant(@Valid DifyChatAssistantParam params) {
        JsonNode result = difyChatAssistantClient.chatAssistant(params);
        DifyChatAssistantVO vo = new DifyChatAssistantVO();
        if (result != null && !result.isMissingNode()) {
            vo.setAnswer(result.path("answer").asText(null));
            vo.setConversation_id(result.path("conversation_id").asText(null));
        }
        return vo;

    }
}