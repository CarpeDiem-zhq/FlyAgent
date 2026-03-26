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

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("product_feature")
@Schema(description = "产品功能")
public class ProductFeatureEntity implements ProductFeatureEntityGetter {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @TableField("product_id")
    @Schema(description = "产品id")
    private Long productId;

    @TableField("feature_name")
    @Schema(description = "功能名称")
    private String featureName;

    @TableField("enabled")
    @Schema(description = "是否启用")
    private Boolean enabled;

    @TableField("del")
    @Schema(description = "是否删除")
    private Boolean del;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    public ProductFeatureEntity(ProductFeatureEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.featureName = source.getFeatureName();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    public ProductFeatureEntity cloneEntity() {
        return new ProductFeatureEntity(this);
    }
}
