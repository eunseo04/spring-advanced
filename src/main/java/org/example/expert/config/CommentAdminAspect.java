package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommentAdminAspect {
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long startTime = System.currentTimeMillis();
        Object[] args = pjp.getArgs();
        Comment comment = commentRepository.findById((Long) args[0])
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        System.out.println("API 요청 시각 " + startTime);
        System.out.println("API 요청 URL" + request.getRequestURI());
        System.out.println("요청한 사용자의 ID: " + comment.getUser().getId());
        System.out.println("RequestBody(JSON): " + objectMapper.writeValueAsString(args[0]));

        Object result = pjp.proceed();

        System.out.println("ResponseBody: " + objectMapper.writeValueAsString(result));

        return result;
    }
}