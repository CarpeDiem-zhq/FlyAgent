package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本生成批次
 *
 * @author codex
 * @since 2026-02-27
 */
public interface ScriptGenerateBatchEntityGetter extends EntityCloner<ScriptGenerateBatchEntity> {

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
     * 请求生成条数
     */
    Integer getAdNumber();
    /**
     * 来源类型
     */
    String getSourceType();
    /**
     * 成功条数
     */
    Integer getSuccessCount();
    /**
     * 失败条数
     */
    Integer getFailCount();
    /**
     * 状态：0.处理中 1.完成 2.部分失败 3.失败
     */
    Integer getStatus();
    /**
     * 请求快照
     */
    String getRequestSnapshot();
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
    ScriptGenerateBatchEntity cloneEntity();
}
