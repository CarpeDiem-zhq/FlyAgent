package cn.yeezi.model.vo;

import lombok.Data;

@Data
public class DifyChatAssistantVO {
    /**
     * 响应结果
     */
    private String answer;

    /**
     * 会话ID
     */
    private String conversation_id;
}
