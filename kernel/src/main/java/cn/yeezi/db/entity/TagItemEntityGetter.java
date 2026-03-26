package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 标签项
 *
 * @author codex
 * @since 2026-01-12
 */
public interface TagItemEntityGetter extends EntityCloner<TagItemEntity> {

    /**
     * 主键
     */
    Long getId();

    /**
     * 产品id
     */
    Long getProductId();

    /**
     * 标签组id
     */
    Long getGroupId();

    /**
     * 标签名称
     */
    String getTagName();

    /**
     * 所属产品功能标签项id
     */
    Long getFeatureItemId();

    /**
     * 是否启用：0.否 1.是
     */
    Boolean getEnabled();

    /**
     * 排序
     */
    Integer getSortOrder();

    /**
     * 是否删除：0.否 1.是
     */
    Boolean getDel();

    /**
     * 创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * 更新时间
     */
    LocalDateTime getUpdateTime();

    @Override
    TagItemEntity cloneEntity();
}
