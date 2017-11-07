package com.core.smart.helper;

import com.core.smart.annotation.Controller;
import com.core.smart.annotation.Service;
import com.core.smart.tools.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 类操作帮助类(获取注解了对应注解的类)
 * Created by Administrator on 2017/11/7.
 */
public final class ClassHelper {
    /**
     * 定义类集合，存放所加载的类
     */
    private static final Set<Class<?>> CLASS_SET;

    static {
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    /**
     * 获取应用包名下所有的类
     */
    public static Set<Class<?>> getClassSet(){
        return CLASS_SET;
    }

    /**
     * 获取应用包名下所有的Service类
     */
    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls:CLASS_SET){
            if (cls.isAnnotationPresent(Service.class)){
                classSet.add(cls);
            }
        }
        return classSet;
    }


    /**
     * 获取应用包名下所有的Controller类
     */
    public static Set<Class<?>> getControllerClassSet(){
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls:CLASS_SET){
            if (cls.isAnnotationPresent(Controller.class)){
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用包名下所有Bean类（包括service，controller等）
     */
    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> beanClassSet = new HashSet<>();
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        return beanClassSet;
    }
}
