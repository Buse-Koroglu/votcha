package com.example.votcha.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SystemActionLogger {
    private static final Logger log = LoggerFactory.getLogger(SystemActionLogger.class);

    public void execute(String domain, String action, String details, String user, Runnable task){
        long start = System.currentTimeMillis();

        MDC.put("domain", domain);
        MDC.put("action", action);
        MDC.put("user", user);
        MDC.put("details", details);
        MDC.put("STATUS", "SUCCESS");

        try{
            task.run();
            long duration = System.currentTimeMillis() - start;
            MDC.put("duration", String.valueOf(duration));
            log.info("BUSINESS_ACTION_SUCCESS: {} | Duration: {}ms", action, duration);
        }catch(Exception e){
            long duration = System.currentTimeMillis() - start;
            MDC.put("duration", String.valueOf(duration));
            MDC.put("STATUS", "FAIL");
            log.error("BUSINESS_ACTION_FAILED: {} | Duration: {}ms | Error: {}", action, duration, e.getMessage(), e);
            throw e;
        }finally {
            MDC.clear();
        }

    }
}
