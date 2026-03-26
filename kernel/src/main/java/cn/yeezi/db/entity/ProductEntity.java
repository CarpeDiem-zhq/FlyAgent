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
 * 产品
 *
 * @author codex
 * @since 2025-12-19
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("product")
@Schema(description = "产品")
public class ProductEntity implements ProductEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品名称")
    @TableField("product_name")
    private String productName;

    @Schema(description = "类目id")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ProductEntity(ProductEntityGetter source) {
        this.id = source.getId();
        this.productName = source.getProductName();
        this.categoryId = source.getCategoryId();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ProductEntity cloneEntity() {
        return new ProductEntity(this);
    }
}
