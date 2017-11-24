package com.core.server.cache;

import com.core.server.tools.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.Enumeration;

/**
 * Created by Administrator on 2017/11/24.
 */
public class RedisSessionCache extends AbstractCache {
    private CacheType type;
    private Jedis jedis;

    public RedisSessionCache() {
        this.type = CacheType.REDIS;
        this.jedis = null;
        this.jedis = RedisUtils.getConnection();
        RedisUtils.freeConnection(this.jedis);
    }

    public void freeConnection() {
        RedisUtils.freeConnection(this.jedis);
    }

    public CacheType getType() {
        return this.type;
    }

    public Object getParam(String name) throws Exception {
        return RedisUtils.getParam(name, this.jedis);
    }

    public void setParam(String name, Object value, int seconds) throws Exception {
        if(seconds > 0) {
            RedisUtils.setParam(name, value, this.jedis, seconds);
        } else {
            RedisUtils.setParam(name, value, this.jedis);
        }

    }

    public void expire(String name, long liveTime) throws Exception {
        Long l = Long.valueOf(liveTime);
        String type = this.jedis.hget("param_type", name);
        if("s".equals(type)) {
            this.jedis.expire(name, l.intValue());
        } else if("b".equals(type)) {
            this.jedis.expire(name.getBytes(), l.intValue());
        }

    }

    public Enumeration<String> keys() {
        return (Enumeration)this.jedis.keys("*");
    }
}
