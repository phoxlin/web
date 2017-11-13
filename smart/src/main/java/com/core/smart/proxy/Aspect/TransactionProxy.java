package com.core.smart.proxy.Aspect;

import com.core.smart.annotation.Transaction;
import com.core.smart.helper.DatabaseHelper;
import com.core.smart.proxy.Proxy;
import com.core.smart.proxy.ProxyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 事务代理切面类
 * Created by Administrator on 2017/11/11.
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);

    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>(){
        protected Boolean initialValue(){
            return false;
        }
    };


    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable
    {
        Object result ;
        boolean flag = FLAG_HOLDER.get();
        Method method = proxyChain.getTargetMethod();
        if (!flag&&method.isAnnotationPresent(Transaction.class)){
            FLAG_HOLDER.set(true);
            try{
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                result = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            }catch (Exception e){
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            }
        }else
            result = proxyChain.doProxyChain();

        return result;
    }
}
