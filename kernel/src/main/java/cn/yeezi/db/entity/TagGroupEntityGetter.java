package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 标签组
 *
 * @author codex
 * @since 2026-01-12
 */
public interface TagGroupEntityGetter extends EntityCloner<TagGroupEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 产品id
     */
    Long getProductId();
    /**
     * 标签组编码
     */
    String getGroupCode();
    /**
     * 标签组名称
     */
    String getGroupName();
    /**
     * 输入类型
     */
    String getInputType();
    /**
     * 是否必填：0.否 1.是
     */
    Boolean getRequired();
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
    TagGroupEntity cloneEntity();
}
