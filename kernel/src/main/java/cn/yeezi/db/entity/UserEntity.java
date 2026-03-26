package cn.yeezi.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author whh
 * @since 2025-12-16
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("user")
@Schema(description = "用户")
public class UserEntity implements UserEntityGetter {

    @Schema(description = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户名")
    @TableField("`name`")
    private String name;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "头像地址")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "密码")
    @TableField("`password`")
    private String password;

    @Schema(description = "状态：1.正常 2.禁用")
    @TableField("`status`")
    private Integer status;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "是否删除：0.否 1.是")
    @TableField("del")
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    public UserEntity(UserEntityGetter source) {
        this.id = source.getId();
        this.name = source.getName();
        this.phone = source.getPhone();
        this.avatar = source.getAvatar();
        this.password = source.getPassword();
        this.status = source.getStatus();
        this.remark = source.getRemark();
        this.del = source.getDel();
        this.createTime = source.getCreateTime();
        this.updateTime = source.getUpdateTime();
    }

    @Override
    public UserEntity cloneEntity(){
        return new UserEntity(this);
    }




}
