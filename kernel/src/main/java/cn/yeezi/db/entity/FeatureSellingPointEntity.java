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
@TableName("feature_selling_point")
@Schema(description = "功能核心卖点")
public class FeatureSellingPointEntity implements FeatureSellingPointEntityGetter {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @TableField("product_id")
    @Schema(description = "产品id")
    private Long productId;

    @TableField("feature_id")
    @Schema(description = "功能id")
    private Long featureId;

    @TableField("selling_point_name")
    @Schema(description = "核心卖点名称")
    private String sellingPointName;

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

    public FeatureSellingPointEntity(FeatureSellingPointEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.featureId = source.getFeatureId();
        this.sellingPointName = source.getSellingPointName();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    public FeatureSellingPointEntity cloneEntity() {
        return new FeatureSellingPointEntity(this);
    }
}
