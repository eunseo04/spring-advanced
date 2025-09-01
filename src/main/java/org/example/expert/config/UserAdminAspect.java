package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserAdminAspect {
    private final ObjectMapper objectMapper;
    @Around("execution(* org.example.expert.domain.user.controller.UserAdminController.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long startTime = System.currentTimeMillis();
        Object[] args = pjp.getArgs();
        String paramsJson = Arrays.stream(args)
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return String.valueOf(arg);
                    }
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        System.out.println("API 요청 시각 " + startTime);
        System.out.println("API 요청 URL" + request.getRequestURI());
        System.out.println("요청한 사용자의 ID: " + args[0]);
        System.out.println("RequestBody(JSON): " + paramsJson);

        Object result = pjp.proceed();

        System.out.println("ResponseBody: " + objectMapper.writeValueAsString(result));

        return result;
    }
}
