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
 * 脚本复制日志
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("script_copy_log")
@Schema(description = "脚本复制日志")
public class ScriptCopyLogEntity implements ScriptCopyLogEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "资产id")
    @TableField("asset_id")
    private Long assetId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    public ScriptCopyLogEntity(ScriptCopyLogEntityGetter source) {
        this.id = source.getId();
        this.assetId = source.getAssetId();
        this.userId = source.getUserId();
        this.createTime = source.getCreateTime();
    }

    @Override
    public ScriptCopyLogEntity cloneEntity() {
        return new ScriptCopyLogEntity(this);
    }
}
