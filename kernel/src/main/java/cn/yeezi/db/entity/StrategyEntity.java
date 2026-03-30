package cn.yeezi.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("strategy")
@Schema(description = "策略")
public class StrategyEntity implements StrategyEntityGetter {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @TableField("strategy_name")
    @Schema(description = "策略名称")
    private String strategyName;

    @TableField("product_id")
    @Schema(description = "产品id")
    private Long productId;

    @TableField("feature_id")
    @Schema(description = "功能id")
    private Long featureId;

    @TableField("feature_name")
    @Schema(description = "功能名称快照")
    private String featureName;

    @TableField("core_selling_point_id")
    @Schema(description = "核心卖点id")
    private Long coreSellingPointId;

    @TableField("core_selling_point_names")
    @Schema(description = "核心卖点名称快照(JSON数组)")
    private String coreSellingPointNames;

    @TableField("target_audience")
    @Schema(description = "目标受众")
    private String targetAudience;

    @TableField("target_scene")
    @Schema(description = "目标场景")
    private String targetScene;

    @TableField("tone_style")
    @Schema(description = "语调风格")
    private String toneStyle;

    @TableField("call_to_action")
    @Schema(description = "行动指令")
    private String callToAction;

    @TableField("ad_words")
    @Schema(description = "字数限制")
    private String adWords;

    @TableField("enabled")
    @Schema(description = "是否启用")
    private Boolean enabled;

    @TableField("del")
    @Schema(description = "是否删除")
    private Boolean del;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    public StrategyEntity(StrategyEntityGetter source) {
        this.id = source.getId();
        this.strategyName = source.getStrategyName();
        this.productId = source.getProductId();
        this.featureId = source.getFeatureId();
        this.featureName = source.getFeatureName();
        this.coreSellingPointId = source.getCoreSellingPointId();
        this.coreSellingPointNames = source.getCoreSellingPointNames();
        this.targetAudience = source.getTargetAudience();
        this.targetScene = source.getTargetScene();
        this.toneStyle = source.getToneStyle();
        this.callToAction = source.getCallToAction();
        this.adWords = source.getAdWords();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    public StrategyEntity cloneEntity() {
        return new StrategyEntity(this);
    }
}
