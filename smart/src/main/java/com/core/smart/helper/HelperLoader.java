package com.core.smart.helper;

import com.core.smart.tools.ClassUtil;

/**
 * 加载相应的Helper类
 * Created by Administrator on 2017/11/9.
 */
public final class HelperLoader {

    /**
     * AopHelper要在IocHelper之前加载，首先需加载AopHelper获取代理，再通过IocHelper进行注入
     */
    public static void init(){
        Class<?>[] classes = {
                ClassHelper.class,
                BeanHelper.class,
                ControllerHelper.class,
                AopHelper.class,
                IocHelper.class
        };
        for (Class<?> cls:classes){
            //System.out.println("init:"+cls.getName());
            ClassUtil.loadClass(cls.getName(),true);
        }
    }

}
