package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本反馈
 *
 * @author codex
 * @since 2026-01-12
 */
public interface ScriptFeedbackEntityGetter extends EntityCloner<ScriptFeedbackEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 资产id
     */
    Long getAssetId();
    /**
     * 用户id
     */
    Long getUserId();
    /**
     * 是否满意：0.否 1.是
     */
    Boolean getSatisfied();
    /**
     * 原因编码列表
     */
    String getReasonCodes();
    /**
     * 建议
     */
    String getSuggestion();
    /**
     * 是否回炉：0.否 1.是
     */
    Boolean getRerun();
    /**
     * 创建时间
     */
    LocalDateTime getCreateTime();

    @Override
    ScriptFeedbackEntity cloneEntity();
}
