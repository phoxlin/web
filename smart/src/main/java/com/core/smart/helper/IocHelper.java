package com.core.smart.helper;

import com.core.smart.annotation.Inject;
import com.core.smart.tools.ArrayUtil;
import com.core.smart.tools.CollectionUtil;
import com.core.smart.tools.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入助手类
 * Created by Administrator on 2017/11/9.
 */
public final class IocHelper {

    static {
        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)){
            for (Class<?> beanClass :beanMap.keySet()){
                Object beanInstance = beanMap.get(beanClass);
                //获取类的所有成员变量
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)){
                    for(Field beanField:beanFields){
                        //判断是否有Inject注解
                        if (beanField.isAnnotationPresent(Inject.class)){
                            Class<?> beanFieldClass = beanField.getType();
                            //从beanMap中获取成员变量的实例
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance!=null){
                                //反射初始化Field的值
                                ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
