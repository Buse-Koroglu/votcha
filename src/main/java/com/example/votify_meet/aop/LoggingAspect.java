package com.example.votify_meet.aop;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /*
    * @Pointcut(***)
    * A point during the execution of a program, such as execution of a method or the handling of an exception
    * In Spring AOP, a join point always represents a method execution.
    */

    /*
    * @Before(**)
    * Advice that executes before a joint point.
    * But it doesn't have the ability to prevent execution flow (unless it throws an exception).
    */

    /*
    * @After(***)
    * Advice that executes after a joint point.
    * Completes normally if a method returns without throwing an exception.
    */

    /*
     * @AfterThrowing(***)
     * Advice to be executed if a method exits by throwing an exception.
     */

    /*
     * @Around(***)
     * Advice that surrounds a join point such as a method invocation.
     * This is the most powerful kind of advice.
     * Around advice can perform custom behavior before and after the method invocation.
     */

    // Business action logs
    @Around("@annotation(businessAction)")
    public Object logBusinessAction(ProceedingJoinPoint joinPoint, BusinessAction businessAction) throws Throwable {
        long start = System.currentTimeMillis();

        MDC.put("domain", businessAction.domain());
        MDC.put("action", businessAction.action());

        String userIdentifier = resolveUserIdentifier(joinPoint.getArgs());
        MDC.put("user", userIdentifier);


        try {
            Object result = joinPoint.proceed();

            recordLog(start, "SUCCESS", null);

            return result;
        }catch (Throwable throwable){
            recordLog(start, "FAIL", throwable.getMessage());
            throw throwable;
        }
        finally {
            removeFromMDC("domain", "user", "action", "duration", "status");
        }
    }

    // Performance logs for all controllers
    @Around("execution(* com.example.votify_meet.*.api.*.*(..))")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            MDC.put("duration", String.valueOf(duration));
            logger.info("REST_API_CALL: {} | Duration: {}ms",
                    joinPoint.getSignature().toShortString(), duration);
            // DO NOT use MDC.clear() here. It will kill your traceId.
            MDC.remove("duration");
        }
    }

    private void recordLog(long startTime, String status, String error){
        long duration = System.currentTimeMillis() - startTime;
        MDC.put("duration", String.valueOf(duration));
        MDC.put("status", status);

        if ("FAIL".equals(status)) {
            logger.error("BUSINESS_ACTION_FAILED: {} | Duration: {}ms | Error: {}",
                    MDC.get("action"), duration, error);
        } else {
            logger.info("BUSINESS_ACTION_SUCCESS: {} | Duration: {}ms",
                    MDC.get("action"), duration);
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


/*

    @Pointcut("execution(* com.example.votify_meet.users.domain.model.*.*(..))")
    public void forUsersEntity() {}

    @Pointcut("execution(* com.example.votify_meet.events.domain.model.*.*(..))")
    public void forEventEntity() {}

    @Pointcut("execution(* com.example.votify_meet.options.domain.model.*.*(..))")
    public void forOptionEntity() {}

    @Pointcut("execution(* com.example.votify_meet.votes.domain.model.*.*(..))")
    public void forVoteEntity() {}

    // This method will run before all the methods above
    @Pointcut("forUsersEntity() | forEventEntity() | forOptionEntity() | forVoteEntity()")
    public void forFlow(){}

    // This method will run before forFlow(), we will manage the logs for each method from here!
    @Before("forFlow()")
    public void beforeFlow(JoinPoint joinPoint){
        String method = joinPoint.getSignature().toShortString();
        logger.info("Before {}", method);

        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
            logger.info("Args {}: ", arg);
        }
    }

    @AfterReturning(pointcut = "forFlow()",
                    returning = "result")
    public void afterReturning(Object result){
        logger.info("Result: {} ", result);
    }
*/
}
