package com.example.votcha.common.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessAction {
    String action(); // VOTE_CAST, EVENT_CREATED, AUTH_FAILURE
    String domain() default "SYS";
    String logDetails() default "";
}
