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
 * 标签项
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tag_item")
@Schema(description = "标签项")
public class TagItemEntity implements TagItemEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "标签组id")
    @TableField("group_id")
    private Long groupId;

    @Schema(description = "标签名称")
    @TableField("tag_name")
    private String tagName;

    @Schema(description = "所属产品功能标签项id")
    @TableField("feature_item_id")
    private Long featureItemId;

    @Schema(description = "是否启用：0.否 1.是")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public TagItemEntity(TagItemEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.groupId = source.getGroupId();
        this.tagName = source.getTagName();
        this.featureItemId = source.getFeatureItemId();
        this.enabled = source.getEnabled();
        this.sortOrder = source.getSortOrder();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public TagItemEntity cloneEntity() {
        return new TagItemEntity(this);
    }
}
