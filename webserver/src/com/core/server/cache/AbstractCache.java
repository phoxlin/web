package com.core.server.cache;

/**
 * Created by Administrator on 2017/11/24.
 */
public abstract class AbstractCache implements Cache{
    public AbstractCache() {
    }

    public void setHParam(String name, String key, Object value, int seconds) throws Exception {
        this.setParam(name + "_##_" + key, value, seconds);
    }

    public void setHParam(String name, String key, Object value) throws Exception {
        this.setHParam(name, key, value, 0);
    }

    public abstract void setParam(String var1, Object var2, int var3) throws Exception;

    public void setParam(String name, Object value) throws Exception {
        this.setParam(name, value, -1);
    }

    public Object getHParam(String name, String key) throws Exception {
        return this.getParam(name + "_##_" + key);
    }

    public abstract Object getParam(String var1) throws Exception;

    public abstract void expire(String var1, long var2) throws Exception;

    public void expire(String name, String key, long liveTime) throws Exception {
        this.expire(name + "_##_" + key, liveTime);
    }

    public abstract void freeConnection();
}
