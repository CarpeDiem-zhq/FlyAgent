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
 * 脚本生成历史
 *
 * @author codex
 * @since 2026-02-27
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_generate_history")
@Schema(description = "脚本生成历史")
public class ScriptGenerateHistoryEntity implements ScriptGenerateHistoryEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "批次id")
    @TableField("batch_id")
    private Long batchId;

    @Schema(description = "批次序号")
    @TableField("item_seq")
    private Integer itemSeq;

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

    @Schema(description = "来源类型")
    @TableField("source_type")
    private String sourceType;

    @Schema(description = "父资产id")
    @TableField("parent_asset_id")
    private Long parentAssetId;

    @Schema(description = "修订序号")
    @TableField("revision_seq")
    private Integer revisionSeq;

    @Schema(description = "是否已保存：0.否 1.是")
    @TableField("save_status")
    private Boolean saveStatus;

    @Schema(description = "已保存资产id")
    @TableField("saved_asset_id")
    private Long savedAssetId;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public ScriptGenerateHistoryEntity(ScriptGenerateHistoryEntityGetter source) {
        this.id = source.getId();
        this.batchId = source.getBatchId();
        this.itemSeq = source.getItemSeq();
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
        this.sourceType = source.getSourceType();
        this.parentAssetId = source.getParentAssetId();
        this.revisionSeq = source.getRevisionSeq();
        this.saveStatus = source.getSaveStatus();
        this.savedAssetId = source.getSavedAssetId();
        this.errorMsg = source.getErrorMsg();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public ScriptGenerateHistoryEntity cloneEntity() {
        return new ScriptGenerateHistoryEntity(this);
    }
}
