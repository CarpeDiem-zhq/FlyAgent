package cn.yeezi.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * 项目环境工具类
 *
 * @author wanghh
 * @since 2021-06-01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvUtil {

    private static final Environment environment = ApplicationUtil.getBean(Environment.class);


    /**
     * 是否是生产环境
     *
     * @return true 是，false 不是
     */
    public static boolean isProd() {
        return environment.getActiveProfiles().length > 0 && Objects.equals("prod", environment.getActiveProfiles()[0]);
    }

    /**
     * 是否是测试环境
     *
     * @return true 是，false 不是
     */
    public static boolean isTest() {
        return environment.getActiveProfiles().length > 0 && Objects.equals("test", environment.getActiveProfiles()[0]);
    }

    /**
     * 是否是生产环境
     *
     * @return true 是，false 不是
     */
    public static boolean isUat() {
        return environment.getActiveProfiles().length > 0 && Objects.equals("uat", environment.getActiveProfiles()[0]);
    }

    /**
     * 是否是本地环境
     *
     * @return true 是，false 不是
     */
    public static boolean isDev() {
        return environment.getActiveProfiles().length > 0 && Objects.equals("dev", environment.getActiveProfiles()[0]);
    }
}
