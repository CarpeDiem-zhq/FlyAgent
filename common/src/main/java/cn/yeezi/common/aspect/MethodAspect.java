package cn.yeezi.common.aspect;

import cn.yeezi.common.annotation.IgnorePrintLog;
import cn.yeezi.common.util.JsonUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MethodAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* cn.yeezi.service..*(..))")
    public void method() {
    }


    @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        IgnorePrintLog classAnnotation = targetClass.getAnnotation(IgnorePrintLog.class);
        IgnorePrintLog methodAnnotation = method.getAnnotation(IgnorePrintLog.class);
        if (classAnnotation != null || methodAnnotation != null) {
            return joinPoint.proceed();
        }
        String target = targetClass.getName() + "#" + method.getName();
        if (target.startsWith("com.baomidou.mybatisplus")) {
            return joinPoint.proceed();
        }
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeConsuming = System.currentTimeMillis() - start;
        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            log.info("{} 参数:{}", target, params);
            if (result instanceof List) {
                int size = ((List<?>) result).size();
                log.info("{} 返回集合条数:{} 耗时:{}ms", target, size, timeConsuming);
            } else if (result instanceof IPage) {
                int size = ((IPage<?>) result).getRecords().size();
                log.info("{} 返回集合条数:{} 耗时:{}ms", target, size, timeConsuming);
            } else {
                String returnObj = "";
                if (Objects.nonNull(result)) {
                    returnObj = JsonUtil.toJson(result);
                }
                log.info("{} 返回值:{} 耗时:{}ms", target, returnObj, timeConsuming);
            }
        } catch (Exception e) {
            log.warn(target + "日志打印失败", e);
        }
        return result;
    }
}
