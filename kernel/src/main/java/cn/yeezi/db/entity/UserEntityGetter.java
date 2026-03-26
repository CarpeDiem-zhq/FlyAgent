package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;

import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author whh
 * @since 2025-12-16
 */
public interface UserEntityGetter extends EntityCloner<UserEntity> {

    /**
    * ID
    */
    Long getId();
    /**
    * 用户名
    */
    String getName();
    /**
    * 手机号
    */
    String getPhone();
    /**
    * 头像地址
    */
    String getAvatar();
    /**
    * 密码
    */
    String getPassword();
    /**
    * 状态：1.正常 2.禁用
    */
    Integer getStatus();
    /**
    * 备注
    */
    String getRemark();
    /**
    * 是否删除：0.否 1.是
    */
    Boolean getDel();
    /**
    * 创建时间
    */
    LocalDateTime getCreateTime();
    /**
    * 更新时间
    */
    LocalDateTime getUpdateTime();

    @Override
    UserEntity cloneEntity();
}
