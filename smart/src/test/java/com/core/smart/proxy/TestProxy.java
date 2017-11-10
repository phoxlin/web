package com.core.smart.proxy;

/**
 * 测试代理
 * Created by Administrator on 2017/11/10.
 */
public class TestProxy {

    public static void main(String[] args){
        testDynamicProxy();
    }

    public static void testDynamicProxy(){
        MyDynamicProxy dynamicProxy = new MyDynamicProxy(new HelloImpl());
        Hello helloProxy =  dynamicProxy.getProxy();
        helloProxy.say("test");
    }
}
