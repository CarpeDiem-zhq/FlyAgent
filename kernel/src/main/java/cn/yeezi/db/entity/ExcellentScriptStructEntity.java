package cn.yeezi.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 优秀脚本结构记录
 *
 * @author codex
 * @since 2026-03-04
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("excellent_script_struct")
@Schema(description = "优秀脚本结构记录")
public class ExcellentScriptStructEntity implements ExcellentScriptStructEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "产品名称")
    @TableField("product_name")
    private String productName;

    @Schema(description = "功能名称")
    @TableField("function_name")
    private String functionName;

    @Schema(description = "优秀脚本")
    @TableField("excellent_script")
    private String excellentScript;

    @Schema(description = "解析后的脚本结构")
    @TableField("structured_script")
    private String structuredScript;

    @Schema(description = "知识库id")
    @TableField("knowledge_dataset_id")
    private String knowledgeDatasetId;

    @Schema(description = "知识库文档id")
    @TableField("knowledge_document_id")
    private String knowledgeDocumentId;

    @Schema(description = "文档块id")
    @TableField("segment_id")
    private String segmentId;

    @Schema(description = "同步状态：SYNCING/SUCCESS/FAILED")
    @TableField("sync_status")
    private String syncStatus;

    @Schema(description = "同步失败原因")
    @TableField("sync_error_msg")
    private String syncErrorMsg;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ExcellentScriptStructEntity(ExcellentScriptStructEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.productName = source.getProductName();
        this.functionName = source.getFunctionName();
        this.excellentScript = source.getExcellentScript();
        this.structuredScript = source.getStructuredScript();
        this.knowledgeDatasetId = source.getKnowledgeDatasetId();
        this.knowledgeDocumentId = source.getKnowledgeDocumentId();
        this.segmentId = source.getSegmentId();
        this.syncStatus = source.getSyncStatus();
        this.syncErrorMsg = source.getSyncErrorMsg();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ExcellentScriptStructEntity cloneEntity() {
        return new ExcellentScriptStructEntity(this);
    }
}
