package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 全局规则
 *
 * @author codex
 * @since 2025-12-19
 */
public interface GlobalRuleEntityGetter extends EntityCloner<GlobalRuleEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 规则内容
     */
    String getRuleContent();
    /**
     * 版本号
     */
    Integer getVersion();
    /**
     * 是否启用：0.否 1.是
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
    GlobalRuleEntity cloneEntity();
}
