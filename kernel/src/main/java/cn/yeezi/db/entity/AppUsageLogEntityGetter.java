package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 应用使用埋点日志
 *
 * @author codex
 * @since 2026-03-23
 */
public interface AppUsageLogEntityGetter extends EntityCloner<AppUsageLogEntity> {

    /**
     * 主键
     */
    Long getId();

    /**
     * 用户id
     */
    Long getUserId();

    /**
     * 应用类型编码
     */
    String getAppCode();

    /**
     * 创建时间
     */
    LocalDateTime getCreateTime();

    @Override
    AppUsageLogEntity cloneEntity();
}
