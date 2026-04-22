package com.example.votcha.common.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticSync {
    String action() default "ELASTIC_SYNC";
    String domain() default "SEARCH_MODULE";
    String index() default "default-index";
    String logDetails() default "";

}
