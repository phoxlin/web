package com.core.smart.annotation;

import java.lang.annotation.*;

/**
 * 切面注解
 * Created by Administrator on 2017/11/11.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * 注解
     */
    Class<? extends Annotation> value();
}
