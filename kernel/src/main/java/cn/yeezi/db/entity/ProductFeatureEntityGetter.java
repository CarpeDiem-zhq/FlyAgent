package cn.yeezi.db.entity;

import java.time.LocalDateTime;

public interface ProductFeatureEntityGetter {

    Long getId();

    Long getProductId();

    String getFeatureName();

    Boolean getEnabled();

    Boolean getDel();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();
}
