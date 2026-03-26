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
 * 脚本生成记录
 *
 * @author codex
 * @since 2025-12-19
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_generation_record")
@Schema(description = "脚本生成记录")
public class ScriptGenerationRecordEntity implements ScriptGenerationRecordEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "规则快照")
    @TableField("rule_snapshot")
    private String ruleSnapshot;

    @Schema(description = "输入快照")
    @TableField("input_snapshot")
    private String inputSnapshot;

    @Schema(description = "生成内容")
    @TableField("output_content")
    private String outputContent;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ScriptGenerationRecordEntity(ScriptGenerationRecordEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.ruleSnapshot = source.getRuleSnapshot();
        this.inputSnapshot = source.getInputSnapshot();
        this.outputContent = source.getOutputContent();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ScriptGenerationRecordEntity cloneEntity() {
        return new ScriptGenerationRecordEntity(this);
    }
}
