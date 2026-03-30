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

/**
 * 脚本资产
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_asset")
@Schema(description = "脚本资产")
public class ScriptAssetEntity implements ScriptAssetEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "功能id")
    @TableField("feature_id")
    private Long featureId;

    @Schema(description = "核心卖点id")
    @TableField("core_selling_point_id")
    private Long coreSellingPointId;

    @Schema(description = "核心卖点id列表(JSON数组)")
    @TableField("core_selling_point_ids")
    private String coreSellingPointIds;

    @Schema(description = "策略id")
    @TableField("strategy_id")
    private Long strategyId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "提示词id")
    @TableField("prompt_id")
    private Long promptId;

    @Schema(description = "系统提示词快照")
    @TableField("system_prompt_snapshot")
    private String systemPromptSnapshot;

    @Schema(description = "策略快照")
    @TableField("strategy_snapshot")
    private String strategySnapshot;

    @Schema(description = "功能快照")
    @TableField("feature_snapshot")
    private String featureSnapshot;

    @Schema(description = "卖点快照")
    @TableField("selling_point_snapshot")
    private String sellingPointSnapshot;

    @Schema(description = "脚本标题")
    @TableField("script_title")
    private String scriptTitle;

    @Schema(description = "脚本内容")
    @TableField("script_content")
    private String scriptContent;

    @Schema(description = "模型名称")
    @TableField("model_name")
    private String modelName;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ScriptAssetEntity(ScriptAssetEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.featureId = source.getFeatureId();
        this.coreSellingPointId = source.getCoreSellingPointId();
        this.coreSellingPointIds = source.getCoreSellingPointIds();
        this.strategyId = source.getStrategyId();
        this.userId = source.getUserId();
        this.promptId = source.getPromptId();
        this.systemPromptSnapshot = source.getSystemPromptSnapshot();
        this.strategySnapshot = source.getStrategySnapshot();
        this.featureSnapshot = source.getFeatureSnapshot();
        this.sellingPointSnapshot = source.getSellingPointSnapshot();
        this.scriptTitle = source.getScriptTitle();
        this.scriptContent = source.getScriptContent();
        this.modelName = source.getModelName();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ScriptAssetEntity cloneEntity() {
        return new ScriptAssetEntity(this);
    }
}
