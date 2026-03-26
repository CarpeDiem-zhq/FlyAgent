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
 * 标签组
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tag_group")
@Schema(description = "标签组")
public class TagGroupEntity implements TagGroupEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "标签组编码")
    @TableField("group_code")
    private String groupCode;

    @Schema(description = "标签组名称")
    @TableField("group_name")
    private String groupName;

    @Schema(description = "输入类型")
    @TableField("input_type")
    private String inputType;

    @Schema(description = "是否必填：0.否 1.是")
    @TableField("required")
    private Boolean required;

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

    public TagGroupEntity(TagGroupEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.groupCode = source.getGroupCode();
        this.groupName = source.getGroupName();
        this.inputType = source.getInputType();
        this.required = source.getRequired();
        this.sortOrder = source.getSortOrder();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public TagGroupEntity cloneEntity() {
        return new TagGroupEntity(this);
    }
}
