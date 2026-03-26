package cn.yeezi.common.aspect;

import cn.yeezi.common.annotation.IgnorePrintLog;
import com.alibaba.excel.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Enumeration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerAspect {

    private final ObjectMapper objectMapper;

    //不需要打印参数日志的接口
    public static final String[] urls = new String[]{
//            "/ding/logout"
    };

    @SuppressWarnings("ignore")
    @Pointcut("execution(* cn.yeezi.controller..*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void paramPrint(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        IgnorePrintLog classAnnotation = targetClass.getAnnotation(IgnorePrintLog.class);
        IgnorePrintLog methodAnnotation = method.getAnnotation(IgnorePrintLog.class);
        if (classAnnotation != null || methodAnnotation != null) {
            return;
        }
        String target = targetClass.getName() + "#" + method.getName();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            String requestUrl = request.getRequestURL().toString();
            for (String url : urls) {
                if (requestUrl.contains(url)) {
                    return;
                }
            }
            StringBuilder requestInfo = new StringBuilder();
            requestInfo.append("请求:").append(requestUrl);
            String type = request.getMethod();
            requestInfo.append(" 方式:").append(type);
            requestInfo.append(" IP:").append(request.getRemoteAddr());

            switch (type) {
                case "GET":
                    //获取问号拼接的参数：
                    requestInfo.append(" 参数:");
                    Enumeration<String> enumeration = request.getParameterNames();
                    while (enumeration.hasMoreElements()) {
                        String paramName = enumeration.nextElement();
                        String parameter = request.getParameter(paramName);
                        if (!StringUtils.isEmpty(parameter)) {
                            requestInfo.append(paramName).append(":").append(parameter).append("&");
                        }
                    }
                    break;
                case "POST":
                    Object[] arguments = joinPoint.getArgs();
                    if (arguments.length > 0) {
                        String params = objectMapper.writeValueAsString(arguments);
                        requestInfo.append(" 参数:").append(params);
                    }
                    break;
            }
            log.info(requestInfo.toString());
        } catch (Exception e) {
            log.warn(target + "请求参数打印失败", e);
        }
    }
}
