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
 * 产品规则
 *
 * @author codex
 * @since 2025-12-19
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("rule_product")
@Schema(description = "产品规则")
public class ProductRuleEntity implements ProductRuleEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "规则内容")
    @TableField("rule_content")
    private String ruleContent;

    @Schema(description = "版本号")
    @TableField("version")
    private Integer version;

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

    public ProductRuleEntity(ProductRuleEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.ruleContent = source.getRuleContent();
        this.version = source.getVersion();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ProductRuleEntity cloneEntity() {
        return new ProductRuleEntity(this);
    }
}
