package cn.yeezi.db.entity;

import java.time.LocalDateTime;

public interface StrategyEntityGetter {

    Long getId();

    String getStrategyName();

    Long getProductId();

    Long getFeatureId();

    Long getCoreSellingPointId();

    String getTargetAudience();

    String getTargetScene();

    String getToneStyle();

    String getCallToAction();

    String getAdWords();

    Long getPromptId();

    Boolean getEnabled();

    Boolean getDel();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();
}
