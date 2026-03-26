package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 优秀脚本结构记录
 *
 * @author codex
 * @since 2026-03-04
 */
@Data
@Schema(description = "优秀脚本结构记录")
public class ExcellentScriptStructRecordVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "功能名称")
    private String functionName;

    @Schema(description = "优秀脚本")
    private String excellentScript;

    @Schema(description = "解析后的脚本结构")
    private String structuredScript;

    @Schema(description = "知识库id")
    private String knowledgeDatasetId;

    @Schema(description = "知识库文档id")
    private String knowledgeDocumentId;

    @Schema(description = "文档块id")
    private String segmentId;

    @Schema(description = "同步状态：SYNCING/SUCCESS/FAILED")
    private String syncStatus;

    @Schema(description = "同步失败原因")
    private String syncErrorMsg;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
