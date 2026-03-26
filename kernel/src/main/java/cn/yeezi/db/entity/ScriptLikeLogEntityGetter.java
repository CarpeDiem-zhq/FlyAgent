package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 脚本点赞日志
 *
 * @author codex
 * @since 2026-01-12
 */
public interface ScriptLikeLogEntityGetter extends EntityCloner<ScriptLikeLogEntity> {

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
     * 创建时间
     */
    LocalDateTime getCreateTime();

    @Override
    ScriptLikeLogEntity cloneEntity();
}
