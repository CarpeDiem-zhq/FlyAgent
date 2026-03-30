package cn.yeezi.db.entity;

import java.time.LocalDateTime;

public interface StrategySellingPointEntityGetter {

    Long getId();

    Long getStrategyId();

    Long getProductId();

    Long getFeatureId();

    Long getSellingPointId();

    String getSellingPointName();

    Boolean getDel();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();
}
