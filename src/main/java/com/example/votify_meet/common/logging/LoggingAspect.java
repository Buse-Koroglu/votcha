package com.example.votify_meet.common.logging;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    // Business action logs
    @Around("@annotation(businessAction)")
    public Object logBusinessAction(ProceedingJoinPoint joinPoint, BusinessAction businessAction) throws Throwable {
        String dynamicDetails = parseSpel(businessAction.logDetails(), joinPoint);
        String userIdentifier = resolveUserIdentifier(joinPoint.getArgs());

        return executeWithLogging(
                joinPoint,
                businessAction.domain(),
                businessAction.action(),
                dynamicDetails,
                userIdentifier,
                "BUSINESS_ACTION"
        );
    }

    // Performance logs for all controllers
    @Around("execution(* com.example.votify_meet.*.api.*.*(..))")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String userIdentifier = resolveUserIdentifier(joinPoint.getArgs());
        String actionName = joinPoint.getSignature().toShortString();

        return executeWithLogging(
                joinPoint,
                "API_PERFORMANCE",
                actionName,
                null,
                userIdentifier,
                "REST_API_CALL"
        );
    }

    private Object executeWithLogging(ProceedingJoinPoint joinPoint, String domain, String action,
                                      String details, String userIdentifier, String logPrefix) throws Throwable {
        long start = System.currentTimeMillis();

        MDC.put("domain", domain);
        MDC.put("action", action);
        MDC.put("user", userIdentifier);
        if(details != null && !details.isBlank()) {
            MDC.put("details", details);
        }

        try{
            Object result = joinPoint.proceed();

            recordLog(start, logPrefix, action, "SUCCESS", null);
            return result;
        }catch (Throwable throwable){
            recordLog(start, logPrefix, action, "FAIL", throwable);
            throw throwable;
        }finally {
            removeFromMDC("domain", "user", "action", "duration", "status", "details");
        }
    }

    private void recordLog(long startTime, String logPrefix, String action, String status, Throwable throwable) {
        long duration = System.currentTimeMillis() - startTime;
        MDC.put("duration", String.valueOf(duration));
        MDC.put("status", status);

        if ("FAIL".equals(status)) {
            String errorMessage = (throwable != null) ? throwable.getMessage() : "Unknown error";

            if (isSystemError(throwable)) {
                logger.error("{}_FAILED: {} | Duration: {}ms | Error: {}", logPrefix, action, duration, errorMessage, throwable);
            } else {
                logger.warn("{}_FAILED: {} | Duration: {}ms | Error: {}", logPrefix, action, duration, errorMessage);
            }

        } else {
            logger.info("{}_SUCCESS: {} | Duration: {}ms", logPrefix, action, duration);
        }
    }

    public void removeFromMDC(String ... args){
        for(String arg : args){
            MDC.remove(arg);
        }
    }

    private String resolveUserIdentifier(Object[] args){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        for (Object arg : args) {
            // Register
            if(arg instanceof AuthRequestDto dto) {
                return dto.email();
            }
            // Login
            if(arg instanceof UsersRequestDto dto) {
                return dto.email();
            }
        }
        return "anonymousUser";
    }

    private String parseSpel(String expression, JoinPoint joinPoint){
        if (expression == null || expression.isBlank()) return "";

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Add to the context the method parameters
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        try {
            // If the expression starts with '#', then parse it, if not return directly
            return parser.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            return expression; // Print the log if there is an error
        }
    }

    // Determine the error type (System error, Exception or another)
    private boolean isSystemError(Throwable throwable){
        return throwable == null || throwable.getClass().getName().startsWith("java.");
    }

}
