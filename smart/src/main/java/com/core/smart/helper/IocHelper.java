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

    private static final Map<Class<?>,Object> beanMap;

    static {
        beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)){
            for (Class<?> beanClass :beanMap.keySet()){
                Object beanInstance = beanMap.get(beanClass);
                Field[] beanFields = beanClass.getFields();
                if (ArrayUtil.isNotEmpty(beanFields)){
                    for(Field beanField:beanFields){
                        Class<?> beanFieldClass = beanField.getType();
                        if (beanFieldClass.isAnnotationPresent(Inject.class)){
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance!=null){
                                ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
