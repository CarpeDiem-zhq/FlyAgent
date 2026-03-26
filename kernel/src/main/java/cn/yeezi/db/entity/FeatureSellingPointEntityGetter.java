package cn.yeezi.db.entity;

import java.time.LocalDateTime;

public interface FeatureSellingPointEntityGetter {

    Long getId();

    Long getProductId();

    Long getFeatureId();

    String getSellingPointName();

    Boolean getEnabled();

    Boolean getDel();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();
}
