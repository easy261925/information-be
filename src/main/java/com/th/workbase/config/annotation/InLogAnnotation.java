package com.th.workbase.config.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InLogAnnotation {
    String type() default "";

    String name() default "";
}
