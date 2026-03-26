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
 * 脚本反馈
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_feedback")
@Schema(description = "脚本反馈")
public class ScriptFeedbackEntity implements ScriptFeedbackEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "资产id")
    @TableField("asset_id")
    private Long assetId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "是否满意：0.否 1.是")
    @TableField("satisfied")
    private Boolean satisfied;

    @Schema(description = "原因编码列表")
    @TableField("reason_codes")
    private String reasonCodes;

    @Schema(description = "建议")
    @TableField("suggestion")
    private String suggestion;

    @Schema(description = "是否回炉：0.否 1.是")
    @TableField("rerun")
    private Boolean rerun;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    public ScriptFeedbackEntity(ScriptFeedbackEntityGetter source) {
        this.id = source.getId();
        this.assetId = source.getAssetId();
        this.userId = source.getUserId();
        this.satisfied = source.getSatisfied();
        this.reasonCodes = source.getReasonCodes();
        this.suggestion = source.getSuggestion();
        this.rerun = source.getRerun();
        this.createTime = source.getCreateTime();
    }

    @Override
    public ScriptFeedbackEntity cloneEntity() {
        return new ScriptFeedbackEntity(this);
    }
}
