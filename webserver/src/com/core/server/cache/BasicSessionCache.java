package com.core.server.cache;

import java.util.Enumeration;

/**
 * Created by Administrator on 2017/11/24.
 */
public class BasicSessionCache extends AbstractCache {
    private SessionCacheStorege store = SessionCacheStorege.getInstance();
    private CacheType type;

    public BasicSessionCache() {
        this.type = CacheType.SYSTEM;
    }

    public CacheType getType() {
        return this.type;
    }

    public Object getParam(String name) throws Exception {
        return this.store.get(name);
    }

    public void setParam(String name, Object value, int seconds) throws Exception {
        this.store.put(name, value);
        if(seconds > 0) {
            this.store.expire(name, (long)seconds);
        }

    }

    public void expire(String name, long liveTime) throws Exception {
        this.store.expire(name, liveTime);
    }

    public Enumeration<String> keys() {
        return this.store.keys();
    }

    public void freeConnection() {
    }
}
