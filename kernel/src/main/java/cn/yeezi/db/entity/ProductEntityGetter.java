package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 产品
 *
 * @author codex
 * @since 2025-12-19
 */
public interface ProductEntityGetter extends EntityCloner<ProductEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 产品名称
     */
    String getProductName();

    /**
     * 产品描述
     */
    String getProductDesc();

    /**
     * 是否启用
     */
    Boolean getEnabled();
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
    ProductEntity cloneEntity();
}
