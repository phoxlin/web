package com.core.server.cache;

import java.util.Enumeration;

/**
 * Created by Administrator on 2017/11/24.
 */
public interface Cache {
    void setHParam(String var1, String var2, Object var3, int var4) throws Exception;

    void setHParam(String var1, String var2, Object var3) throws Exception;

    void setParam(String var1, Object var2, int var3) throws Exception;

    void setParam(String var1, Object var2) throws Exception;

    Object getHParam(String var1, String var2) throws Exception;

    Object getParam(String var1) throws Exception;

    void expire(String var1, long var2) throws Exception;

    void expire(String var1, String var2, long var3) throws Exception;

    CacheType getType();

    Enumeration<String> keys();

    void freeConnection();
}
