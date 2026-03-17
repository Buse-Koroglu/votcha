package com.example.votify_meet.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/*
* Here, a trace_id is generated for each request,
* allowing us to track the request's lifecycle to see
* where an error occurred and whether it was successful.
* */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this runs before any other filter
public class TraceIdFilter extends OncePerRequestFilter {
    private final String TRACE_ID_KEY = "trace_id";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Generate a unique ID
            String traceId = crateTraceId();

            // Put the ID into Mapped Diagnostic Context (MDC)
            MDC.put(TRACE_ID_KEY,  traceId);

            // Return the ID in response headers for frontend troubleshooting
            response.addHeader("X-Trace-Id", traceId);


            filterChain.doFilter(request, response);
        }finally {
            // Crucial: Clear the MDC after the request is finished to prevent memory leaks
            // and log pollution in thread-pooled environments
            MDC.remove(TRACE_ID_KEY);
        }
    }

    private String crateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
