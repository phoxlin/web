package com.core.smart.proxy;

/**
 * 测试代理
 * Created by Administrator on 2017/11/10.
 */
public class TestProxy {

    public static void main(String[] args){
        //testDynamicProxy();
        testCGLibProxy();
    }

    /**
     * CGLib动态代理
     */
    public static void testCGLibProxy(){
        Hello helloProxy = CGLibProxy.getCgLibProxy().getProxy(HelloImpl.class);
        helloProxy.say("test CGLib not new ");
    }

    /**
     * CGLib动态代理
     */
    /*public static void testCGLibProxy(){
        CGLibProxy cgLibProxy = new CGLibProxy();
        Hello helloProxy = cgLibProxy.getProxy(HelloImpl.class);
        helloProxy.say("test CGLib");
    }*/

    /**
     * JDK动态代理
     */
    public static void testDynamicProxy(){
        MyDynamicProxy dynamicProxy = new MyDynamicProxy(new HelloImpl());
        Hello helloProxy =  dynamicProxy.getProxy();
        helloProxy.say("test");
    }
}
