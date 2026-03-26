package cn.yeezi.config;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class OpenClawAuthConfiguration implements WebMvcConfigurer {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${openclaw.auth-token:}")
    private String authToken;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                        validateToken(request);
                        return true;
                    }
                })
                .addPathPatterns("/internal/openclaw/script/**");
    }

    private void validateToken(HttpServletRequest request) {
        if (!StringUtils.hasText(authToken)) {
            log.error("openclaw.auth-token 未配置，拒绝访问 {}", request.getRequestURI());
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN, "OpenClaw 鉴权失败");
        }
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN, "OpenClaw 鉴权失败");
        }
        String requestToken = authorization.substring(BEARER_PREFIX.length()).trim();
        if (!authToken.equals(requestToken)) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN, "OpenClaw 鉴权失败");
        }
    }
}
