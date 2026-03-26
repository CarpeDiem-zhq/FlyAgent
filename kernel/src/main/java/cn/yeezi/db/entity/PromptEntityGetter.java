package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 提示词配置
 *
 * @author codex
 * @since 2026-01-13
 */
public interface PromptEntityGetter extends EntityCloner<PromptEntity> {

    /**
     * 主键
     */
    Long getId();

    /**
     * 产品id
     */
    Long getProductId();

    /**
     * 提示词名称
     */
    String getPromptName();

    /**
     * 系统提示词
     */
    String getSystemPrompt();

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
    PromptEntity cloneEntity();
}