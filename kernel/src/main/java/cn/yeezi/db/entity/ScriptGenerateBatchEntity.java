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
 * 脚本生成批次
 *
 * @author codex
 * @since 2026-02-27
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_generate_batch")
@Schema(description = "脚本生成批次")
public class ScriptGenerateBatchEntity implements ScriptGenerateBatchEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "提示词id")
    @TableField("prompt_id")
    private Long promptId;

    @Schema(description = "请求生成条数")
    @TableField("ad_number")
    private Integer adNumber;

    @Schema(description = "来源类型")
    @TableField("source_type")
    private String sourceType;

    @Schema(description = "成功条数")
    @TableField("success_count")
    private Integer successCount;

    @Schema(description = "失败条数")
    @TableField("fail_count")
    private Integer failCount;

    @Schema(description = "状态：0.处理中 1.完成 2.部分失败 3.失败")
    @TableField("status")
    private Integer status;

    @Schema(description = "请求快照")
    @TableField("request_snapshot")
    private String requestSnapshot;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ScriptGenerateBatchEntity(ScriptGenerateBatchEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.userId = source.getUserId();
        this.promptId = source.getPromptId();
        this.adNumber = source.getAdNumber();
        this.sourceType = source.getSourceType();
        this.successCount = source.getSuccessCount();
        this.failCount = source.getFailCount();
        this.status = source.getStatus();
        this.requestSnapshot = source.getRequestSnapshot();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ScriptGenerateBatchEntity cloneEntity() {
        return new ScriptGenerateBatchEntity(this);
    }
}
