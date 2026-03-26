package cn.yeezi.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 分布式id生成工具类
 *
 * @author wanghh
 * @since 2022-08-17
 */
@Component
@Slf4j
public class IdGenerateUtil {

    private Snowflake snowflake;

    @PostConstruct
    public void init() {
        long workerId = 1;
        String ipv4 = NetUtil.getLocalhostStr();
        if (ipv4 != null) {
            long ip = Long.parseLong(ipv4.replaceAll("\\.", ""));
            workerId = Math.abs(Long.hashCode(ip)) % 32;
        }
        long datacenterId = 1;
        snowflake = new Snowflake(workerId, datacenterId);
    }

    public long generateSnowflakeId() {
        return snowflake.nextId();
    }
}
