package com.core.smart.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注解
 * Created by Administrator on 2017/11/14.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    /**
     * 是否一定要传
     */
    boolean value();
}
