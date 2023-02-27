package com.dangkang.app.common.annotation;

import java.lang.annotation.*;

/**
 * @date 2023/1/31 14:04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApplicationService {

    String ApplicationCode() default "";

    String ApplicationName() default "dangkang";

    String ServiceCode() default "";

    String ServiceName() default "";
}
