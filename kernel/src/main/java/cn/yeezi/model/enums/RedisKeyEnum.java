package cn.yeezi.model.enums;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum RedisKeyEnum {

    SMS_CODE("code:sms:%s_%d", "短信验证码", 5L, TimeUnit.MINUTES),
    IMG_CODE("code:img:%s", "图片验证码", 5L, TimeUnit.MINUTES),
    FEISHU_TENANT_ACCESS_TOKEN("feishu:tenant:access_token", "飞书租户access_token", null, null),
    FEISHU_EVENT("feishu:event:%s", "飞书事件", 10L, TimeUnit.MINUTES),

    KUAISHOU_ACCESS_TOKEN("kuaishou:access_token:%s", "快手access_token", null, null),
    KUAISHOU_REFRESH_TOKEN("kuaishou:refresh_token:%s", "快手refresh_token", null, null),

    OCEAN_ENGINE_ACCESS_TOKEN("oceanengine:access_token:%s", "字节access_token", null, null),
    OCEAN_ENGINE_REFRESH_TOKEN("oceanengine:refresh_token:%s", "字节refresh_token", null, null),

    SYS_MEDIA_BUSINESS_LINE("sys:media:business_line:%d", "合作媒体/业务线", 30L, TimeUnit.DAYS),
    SYS_MANAGER("sys:manager:%d", "员工", 30L, TimeUnit.DAYS),
    SYS_INDUSTRY("sys:industry:%d", "行业", 30L, TimeUnit.DAYS),
    //
    ;

    public final String key;
    public final String text;
    public final Long timeout;
    public final TimeUnit timeUnit;

    // ------------------------------------------- key方法 ------------------------------------------

//    public static String getSMSCodeKey(String phone, SmsCodeSceneTypeEnum sceneTypeEnum) {
//        return String.format(RedisKeyEnum.SMS_CODE.key, phone, sceneTypeEnum.value);
//    }

    public static String getFeishuEventKey(String eventId) {
        return String.format(RedisKeyEnum.FEISHU_EVENT.key, eventId);
    }

    public static String getKuaishouAccessTokenKey(String platformAgentId) {
        return String.format(RedisKeyEnum.KUAISHOU_ACCESS_TOKEN.key, platformAgentId);
    }

    public static String getKuaishouRefreshTokenKey(String platformAgentId) {
        return String.format(RedisKeyEnum.KUAISHOU_REFRESH_TOKEN.key, platformAgentId);
    }

    public static String getOceanEngineAccessTokenKey(String platformAgentId) {
        return String.format(RedisKeyEnum.OCEAN_ENGINE_ACCESS_TOKEN.key, platformAgentId);
    }

    public static String getOceanEngineRefreshTokenKey(String platformAgentId) {
        return String.format(RedisKeyEnum.OCEAN_ENGINE_REFRESH_TOKEN.key, platformAgentId);
    }

    public static String getSysMediaBusinessLineKey(long businessLineId) {
        return String.format(RedisKeyEnum.SYS_MEDIA_BUSINESS_LINE.key, businessLineId);
    }

    public static String getSysManagerKey(long managerId) {
        return String.format(RedisKeyEnum.SYS_MANAGER.key, managerId);
    }

    public static String getSysIndustryKey(long industryId) {
        return String.format(RedisKeyEnum.SYS_INDUSTRY.key, industryId);
    }
}
