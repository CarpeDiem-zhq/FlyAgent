package cn.yeezi.model.param;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DifyChatAssistantParam {
    /**
     * 用户输入/提问内容
     */
    @NotBlank(message = "请输入提问内容")
    @Schema(description = "用户输入/提问内容-必填")
    private String query;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String conversation_id;

    /**
     * 输入参数
     */
    private ObjectNode inputs = new ObjectMapper().createObjectNode();
}
