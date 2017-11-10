package com.core.smart.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLib 动态代理（支持没有接口的类）
 * Created by Administrator on 2017/11/10.
 */
public class CGLibProxy implements MethodInterceptor {

    private static CGLibProxy cgLibProxy = new CGLibProxy();

    private CGLibProxy(){}

    public static CGLibProxy getCgLibProxy(){
        return cgLibProxy;
    }

    public <T> T getProxy(Class<T> cls){
        return (T)Enhancer.create(cls,this);
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable
    {
        before();
        Object result = methodProxy.invokeSuper(o,objects);
        after();
        return result;
    }

    private void before(){
        System.out.println("Before");
    }

    private void after(){
        System.out.println("After");
    }
}
