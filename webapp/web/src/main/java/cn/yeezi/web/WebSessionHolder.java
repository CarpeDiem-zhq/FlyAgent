package cn.yeezi.web;

import cn.dev33.satoken.stp.StpUtil;

import static cn.yeezi.model.constant.ConstantPool.USER_PHONE;

/**
 * @author wanghh
 * @since 2022-11-16
 */
public class WebSessionHolder {

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前登录用户手机号
     */
    public static String getUserPhone() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        return (String) StpUtil.getExtra(USER_PHONE);
    }

}
