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
 * 提示词配置
 *
 * @author codex
 * @since 2026-01-13
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("prompt_config")
@Schema(description = "提示词配置")
public class PromptEntity implements PromptEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "提示词名称")
    @TableField("prompt_name")
    private String promptName;

    @Schema(description = "系统提示词")
    @TableField("system_prompt")
    private String systemPrompt;

    @Schema(description = "是否启用：0.否 1.是")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public PromptEntity(PromptEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.promptName = source.getPromptName();
        this.systemPrompt = source.getSystemPrompt();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public PromptEntity cloneEntity() {
        return new PromptEntity(this);
    }
}