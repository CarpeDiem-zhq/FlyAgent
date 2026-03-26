package cn.yeezi.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.core.user.User;
import cn.yeezi.web.WebSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * @author wanghh
 * @since 2022-08-17
 */
@Slf4j
@Configuration
public class LoginInterceptorConfiguration implements WebMvcConfigurer {

    private final String[] excludeNotLoginPath = {
            //登录
            "/user/login",
            "/user/getImgCode",
            //接口文档
            "/doc.*",
            "/swagger-ui.*",
            "/swagger-resources",
            "/webjars/**",
            "/v3/api-docs/**",
            "/user/register",
            //对话助手
            "/dify/chatAssistant",
            "/internal/openclaw/script/**",
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                            if (handler instanceof ResourceHttpRequestHandler) {
                                return;
                            }
                            try {
                                StpUtil.checkLogin();
                            } catch (NotLoginException e) {
                                log.error("请求{} 未登录，{}", handler, e.getMessage());
                                throw e;
                            }
                            SaRouter.match(StpUtil.isLogin()).check(r -> {
                                try {
                                    long userId = WebSessionHolder.getUserId();
                                    User user = User.get(userId);
                                    user.checkStatus();
                                } catch (BusinessException e) {
                                    Object loginId = StpUtil.getLoginId();
                                    StpUtil.logout(loginId);
                                    throw e;
                                }
                            });
                        })
                )
                .addPathPatterns("/**")
                .excludePathPatterns(excludeNotLoginPath);
    }

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
