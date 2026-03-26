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
 * 应用使用埋点日志
 *
 * @author codex
 * @since 2026-03-23
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("app_usage_log")
@Schema(description = "应用使用埋点日志")
public class AppUsageLogEntity implements AppUsageLogEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "应用类型编码")
    @TableField("app_code")
    private String appCode;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    public AppUsageLogEntity(AppUsageLogEntityGetter source) {
        this.id = source.getId();
        this.userId = source.getUserId();
        this.appCode = source.getAppCode();
        this.createTime = source.getCreateTime();
    }

    @Override
    public AppUsageLogEntity cloneEntity() {
        return new AppUsageLogEntity(this);
    }
}
