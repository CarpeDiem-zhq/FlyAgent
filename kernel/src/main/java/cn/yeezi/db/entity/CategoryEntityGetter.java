package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 产品类目
 *
 * @author codex
 * @since 2026-01-12
 */
public interface CategoryEntityGetter extends EntityCloner<CategoryEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 类目名称
     */
    String getCategoryName();
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
    CategoryEntity cloneEntity();
}
