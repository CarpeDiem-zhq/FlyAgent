package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本生成记录
 *
 * @author codex
 * @since 2025-12-19
 */
public interface ScriptGenerationRecordEntityGetter extends EntityCloner<ScriptGenerationRecordEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 产品id
     */
    Long getProductId();
    /**
     * 规则快照
     */
    String getRuleSnapshot();
    /**
     * 输入快照
     */
    String getInputSnapshot();
    /**
     * 生成内容
     */
    String getOutputContent();
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
    ScriptGenerationRecordEntity cloneEntity();
}
