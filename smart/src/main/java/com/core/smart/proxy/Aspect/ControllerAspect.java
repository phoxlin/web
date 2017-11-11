package com.core.smart.proxy.Aspect;

import com.core.smart.annotation.Aspect;
import com.core.smart.annotation.Controller;
import com.core.smart.proxy.AspectProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 拦截Controller所有方法
 * Created by Administrator on 2017/11/11.
 */
@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy{

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

    private long begin;

    @Override
    public void before(Class<?> cls, Method method, Object[] params)throws Throwable
    {
        LOGGER.debug("=========begin=========");
        LOGGER.debug(String.format("class:%s",cls.getName()));
        LOGGER.debug(String.format("method:%s",method.getName()));
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls,Method method,Object[] params)throws Throwable
    {
        LOGGER.debug(String.format("time:%dms",System.currentTimeMillis()-begin));
        LOGGER.debug("==========end========");
    }

}
