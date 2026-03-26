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
 * 全锟街癸拷锟斤拷
 * @author codex
 * @since 2025-12-19
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("rule_global")
@Schema(description = "全锟街癸拷锟斤拷")
public class GlobalRuleEntity implements GlobalRuleEntityGetter {

    @Schema(description = "锟斤拷锟斤拷")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "锟斤拷锟斤拷锟斤拷锟斤拷")
    @TableField("rule_content")
    private String ruleContent;

    @Schema(description = "锟芥本锟斤拷")
    @TableField("version")
    private Integer version;

    @Schema(description = "锟角凤拷锟斤拷锟矫ｏ拷0.锟斤拷 1.锟斤拷")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "锟角凤拷删锟斤拷锟斤拷0.锟斤拷 1.锟斤拷")
    @TableField("del")
    private Boolean del;

    @Schema(description = "锟斤拷锟斤拷时锟斤拷")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "锟斤拷锟斤拷时锟斤拷")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public GlobalRuleEntity(GlobalRuleEntityGetter source) {
        this.id = source.getId();
        this.ruleContent = source.getRuleContent();
        this.version = source.getVersion();
        this.enabled = source.getEnabled();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public GlobalRuleEntity cloneEntity() {
        return new GlobalRuleEntity(this);
    }
}
