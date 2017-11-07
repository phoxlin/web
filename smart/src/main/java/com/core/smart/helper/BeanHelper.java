package com.core.smart.helper;

import com.core.smart.tools.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bean 助手类
 * Created by Administrator on 2017/11/7.
 */
public final class BeanHelper {

    /**
     * 定义bean映射（用于存放Bean类与其实例的映射关系）
     */
    private static final Map<Class<?>,Object> BEAN_MAP = new HashMap<>();

    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for(Class<?> beanClass:beanClassSet){
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass,obj);
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>,Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取Bean实例
     */
    public static <T> T getBean(Class<?> cls){
        if (!BEAN_MAP.containsKey(cls)){
            throw new RuntimeException("Can not get bean by class:"+cls);
        }

        return (T) BEAN_MAP.get(cls);
    }



}
