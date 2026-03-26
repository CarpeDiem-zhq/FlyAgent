package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本生成历史
 *
 * @author codex
 * @since 2026-02-27
 */
public interface ScriptGenerateHistoryEntityGetter extends EntityCloner<ScriptGenerateHistoryEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 批次id
     */
    Long getBatchId();
    /**
     * 批次序号
     */
    Integer getItemSeq();
    /**
     * 产品id
     */
    Long getProductId();
    /**
     * 用户id
     */
    Long getUserId();
    /**
     * 提示词id
     */
    Long getPromptId();
    /**
     * 提示词快照
     */
    String getPromptSnapshot();
    /**
     * 规则快照
     */
    String getRuleSnapshot();
    /**
     * 输入快照
     */
    String getInputSnapshot();
    /**
     * 标签快照
     */
    String getTagSnapshot();
    /**
     * 用户输入快照
     */
    String getUserInputSnapshot();
    /**
     * 案例快照
     */
    String getCaseSnapshot();
    /**
     * 模型名称
     */
    String getModelName();
    /**
     * 路由策略
     */
    String getRouteStrategy();
    /**
     * 生成内容
     */
    String getOutputContent();
    /**
     * 来源类型
     */
    String getSourceType();
    /**
     * 父资产id
     */
    Long getParentAssetId();
    /**
     * 修订序号
     */
    Integer getRevisionSeq();
    /**
     * 是否已保存：0.否 1.是
     */
    Boolean getSaveStatus();
    /**
     * 已保存资产id
     */
    Long getSavedAssetId();
    /**
     * 错误信息
     */
    String getErrorMsg();
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
    ScriptGenerateHistoryEntity cloneEntity();
}
