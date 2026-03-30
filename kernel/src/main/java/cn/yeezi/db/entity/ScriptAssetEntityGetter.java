package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本资产
 *
 * @author codex
 * @since 2026-01-12
 */
public interface ScriptAssetEntityGetter extends EntityCloner<ScriptAssetEntity> {

    /**
     * 主键
     */
    Long getId();

    /**
     * 产品id
     */
    Long getProductId();

    /**
     * 功能id
     */
    Long getFeatureId();

    /**
     * 核心卖点id
     */
    Long getCoreSellingPointId();

    /**
     * 核心卖点id列表(JSON数组)
     */
    String getCoreSellingPointIds();

    /**
     * 策略id
     */
    Long getStrategyId();

    /**
     * 用户id
     */
    Long getUserId();

    /**
     * 提示词id
     */
    Long getPromptId();

    /**
     * 系统提示词快照
     */
    String getSystemPromptSnapshot();

    /**
     * 策略快照
     */
    String getStrategySnapshot();

    /**
     * 生成功能快照
     */
    String getFeatureSnapshot();

    /**
     * 生成卖点快照
     */
    String getSellingPointSnapshot();

    /**
     * 脚本标题
     */
    String getScriptTitle();

    /**
     * 模型名称
     */
    String getModelName();

    /**
     * 脚本内容
     */
    String getScriptContent();

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
    ScriptAssetEntity cloneEntity();
}
