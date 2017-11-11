package com.core.smart.proxy;

/**
 * 代理接口
 * Created by Administrator on 2017/11/11.
 */
public interface Proxy {

    /**
     * 执行链式代理
     */
    Object doProxy (ProxyChain proxyChain)throws Throwable;
}
