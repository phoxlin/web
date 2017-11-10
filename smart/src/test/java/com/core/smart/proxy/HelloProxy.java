package com.core.smart.proxy;

/**
 * 静态代理
 * Created by Administrator on 2017/11/10.
 */
public class HelloProxy implements Hello {

    private Hello hello;

    public HelloProxy(){
        hello = new HelloImpl();
    }

    @Override
    public void say(String name) {
        before();
        hello.say(name);
        after();
    }

    private void before(){
        System.out.println("Before");
    }

    private void after(){
        System.out.println("After");
    }

    public static void main(String[] args){
        HelloProxy helloProxy = new HelloProxy();
        helloProxy.say("Jack");
    }
}
