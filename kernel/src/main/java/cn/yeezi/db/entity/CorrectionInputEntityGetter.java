package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 纠偏输入
 *
 * @author codex
 * @since 2026-01-12
 */
public interface CorrectionInputEntityGetter extends EntityCloner<CorrectionInputEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 产品id
     */
    Long getProductId();
    /**
     * 资产id
     */
    Long getAssetId();
    /**
     * 输入类型
     */
    String getInputType();
    /**
     * 原因编码列表
     */
    String getReasonCodes();
    /**
     * 建议
     */
    String getSuggestion();
    /**
     * 指标快照
     */
    String getMetricSnapshot();
    /**
     * 创建时间
     */
    LocalDateTime getCreateTime();

    @Override
    CorrectionInputEntity cloneEntity();
}
