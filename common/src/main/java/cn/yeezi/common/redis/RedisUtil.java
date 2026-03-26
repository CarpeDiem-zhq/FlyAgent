package cn.yeezi.common.redis;

import cn.yeezi.common.util.ApplicationUtil;

public class RedisUtil {

    public static final RedisService redisService = ApplicationUtil.getBean(RedisService.class);
}
