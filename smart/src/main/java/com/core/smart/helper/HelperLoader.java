package com.core.smart.helper;

import com.core.smart.tools.ClassUtil;

/**
 * 加载相应的Helper类
 * Created by Administrator on 2017/11/9.
 */
public final class HelperLoader {

    public static void init(){
        Class<?>[] classes = {ClassHelper.class,BeanHelper.class,ControllerHelper.class,IocHelper.class};
        for (Class<?> cls:classes){
            ClassUtil.loadClass(cls.getName(),true);
        }
    }

}
