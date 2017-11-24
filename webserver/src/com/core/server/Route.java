package com.core.server;

import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/11/24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Route {
    String value() default "/";

    HttpMethod[] m() default {HttpMethod.GET};

    boolean conn() default true;

    boolean slave() default false;

    String aliase() default "/";

    ContentType type() default ContentType.JSON;

    boolean slience() default false;

    boolean realSlience() default false;
}
