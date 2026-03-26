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
     * 父资产id
     */
    Long getParentAssetId();

    /**
     * 修订序号
     */
    Integer getRevisionSeq();

    /**
     * 复制次数
     */
    Integer getCopyCount();

    /**
     * 点赞次数
     */
    Integer getLikeCount();

    /**
     * 收藏次数
     */
    Integer getFavoriteCount();

    /**
     * 正向反馈次数
     */
    Integer getPositiveFeedbackCount();

    /**
     * 负向反馈次数
     */
    Integer getNegativeFeedbackCount();

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
