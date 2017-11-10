package com.core.smart.proxy;

/**
 * Created by Administrator on 2017/11/10.
 */
public class HelloImpl implements Hello {

    @Override
    public void say(String name) {
        System.out.println("Hello!"+name);
    }
}
