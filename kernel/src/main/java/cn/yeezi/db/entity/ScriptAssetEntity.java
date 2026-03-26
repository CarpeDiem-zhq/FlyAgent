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

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "提示词id")
    @TableField("prompt_id")
    private Long promptId;

    @Schema(description = "提示词快照")
    @TableField("prompt_snapshot")
    private String promptSnapshot;

    @Schema(description = "规则快照")
    @TableField("rule_snapshot")
    private String ruleSnapshot;

    @Schema(description = "输入快照")
    @TableField("input_snapshot")
    private String inputSnapshot;

    @Schema(description = "标签快照")
    @TableField("tag_snapshot")
    private String tagSnapshot;

    @Schema(description = "用户输入快照")
    @TableField("user_input_snapshot")
    private String userInputSnapshot;

    @Schema(description = "案例快照")
    @TableField("case_snapshot")
    private String caseSnapshot;

    @Schema(description = "模型名称")
    @TableField("model_name")
    private String modelName;

    @Schema(description = "路由策略")
    @TableField("route_strategy")
    private String routeStrategy;

    @Schema(description = "生成内容")
    @TableField("output_content")
    private String outputContent;

    @Schema(description = "父资产id")
    @TableField("parent_asset_id")
    private Long parentAssetId;

    @Schema(description = "修订序号")
    @TableField("revision_seq")
    private Integer revisionSeq;

    @Schema(description = "复制次数")
    @TableField("copy_count")
    private Integer copyCount;

    @Schema(description = "点赞次数")
    @TableField("like_count")
    private Integer likeCount;

    @Schema(description = "收藏次数")
    @TableField("favorite_count")
    private Integer favoriteCount;

    @Schema(description = "正向反馈次数")
    @TableField("positive_feedback_count")
    private Integer positiveFeedbackCount;

    @Schema(description = "负向反馈次数")
    @TableField("negative_feedback_count")
    private Integer negativeFeedbackCount;

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
        this.userId = source.getUserId();
        this.promptId = source.getPromptId();
        this.promptSnapshot = source.getPromptSnapshot();
        this.ruleSnapshot = source.getRuleSnapshot();
        this.inputSnapshot = source.getInputSnapshot();
        this.tagSnapshot = source.getTagSnapshot();
        this.userInputSnapshot = source.getUserInputSnapshot();
        this.caseSnapshot = source.getCaseSnapshot();
        this.modelName = source.getModelName();
        this.routeStrategy = source.getRouteStrategy();
        this.outputContent = source.getOutputContent();
        this.parentAssetId = source.getParentAssetId();
        this.revisionSeq = source.getRevisionSeq();
        this.copyCount = source.getCopyCount();
        this.likeCount = source.getLikeCount();
        this.favoriteCount = source.getFavoriteCount();
        this.positiveFeedbackCount = source.getPositiveFeedbackCount();
        this.negativeFeedbackCount = source.getNegativeFeedbackCount();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ScriptAssetEntity cloneEntity() {
        return new ScriptAssetEntity(this);
    }
}
