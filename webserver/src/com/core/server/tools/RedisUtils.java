package com.core.server.tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisUtils {

    private static JedisPool POOL = new JedisPool(getConfig(),
            Resources.getProperty("Redis_HOST", "127.0.0.1"),
            Resources.getIntProperty("Redis_PORT", 6379), 10000);


    private static JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(Resources.getIntProperty("Redis_MaxActive", 100));
        config.setMaxIdle(Resources.getIntProperty("Redis_MaxIdle", 50));
        config.setMaxWait((long)(Resources.getIntProperty("Redis_MaxWait", 5) * 1000));
        config.setTestOnBorrow(true);
        return config;
    }

    public static Jedis getConnection() {
        return (Jedis)POOL.getResource();
    }

    public static void freeConnection(Jedis jedis) {
        try {
            if(jedis != null) {
                POOL.returnResource(jedis);
            }
        } catch (Exception var2) {
            ;
        }

    }

    public static void setParam(String name, Object value) throws Exception {
        Jedis jedis = null;

        try {
            jedis = getConnection();
            setParam(name, value, jedis);
        } catch (Exception var7) {
            throw var7;
        } finally {
            freeConnection(jedis);
        }

    }

    public static Object getParam(String name) throws Exception {
        Jedis jedis = null;

        Object var4;
        try {
            jedis = getConnection();
            Object e = getParam(name, jedis);
            var4 = e;
        } catch (Exception var7) {
            throw var7;
        } finally {
            freeConnection(jedis);
        }

        return var4;
    }

    public static void setHParam(String name, String key, Object value) throws Exception {
        Jedis jedis = null;

        try {
            jedis = getConnection();
            setHParam(name, key, value, jedis);
        } catch (Exception var8) {
            throw var8;
        } finally {
            freeConnection(jedis);
        }

    }

    public static void setHParam(String name, String key, Object value, int seconds) throws Exception {
        Jedis jedis = null;

        try {
            jedis = getConnection();
            setHParam(name, key, value, jedis, seconds);
        } catch (Exception var9) {
            throw var9;
        } finally {
            freeConnection(jedis);
        }

    }

    public static void setHParam(String name, String key, Object value, Jedis jedis) throws Exception {
        setHParam(name, key, value, jedis, 0);
    }

    public static void setHParam(String name, String key, Object value, Jedis jedis, int seconds) throws Exception {
        if(value == null) {
            jedis.hset("param_type", name + "##" + key, "n");
        } else if(value instanceof String) {
            jedis.hset("param_type", name + "##" + key, "s");
            jedis.hset(name, key, (String)value);
            if(seconds > 0) {
                jedis.expire(name, seconds);
            }
        } else {
            byte[] tempName = name.getBytes();
            jedis.hset("param_type", name + "##" + key, "b");
            jedis.hset(tempName, key.getBytes(), Utils.convertObj2Btyes(value));
            if(seconds > 0) {
                jedis.expire(tempName, seconds);
            }
        }

    }

    public static void setParam(String name, Object value, Jedis jedis, int seconds) throws Exception {
        if(value == null) {
            jedis.hset("param_type", name, "n");
        } else if(value instanceof String) {
            jedis.hset("param_type", name, "s");
            jedis.set(name, (String)value);
            if(seconds > 0) {
                jedis.expire(name, seconds);
            }
        } else {
            byte[] key = name.getBytes();
            jedis.hset("param_type", name, "b");
            jedis.set(key, Utils.convertObj2Btyes(value));
            if(seconds > 0) {
                jedis.expire(key, seconds);
            }
        }

    }

    public static void setParam(String name, Object value, Jedis jedis) throws Exception {
        setParam(name, value, jedis, 0);
    }

    public static Object getParam(String name, Jedis jedis) throws Exception {
        return getParam(name, jedis, 0);
    }

    public static Object getParam(String name, Jedis jedis, int seconds) throws Exception {
        String type = jedis.hget("param_type", name);
        if("n".equals(type)) {
            return null;
        } else if("b".equals(type)) {
            byte[] val2 = jedis.get(name.getBytes());
            if(val2 != null) {
                Object val1 = Utils.convertBytes2Obj(val2);
                if(seconds > 0) {
                    jedis.expire(name.getBytes(), seconds);
                }

                return val1;
            } else {
                return null;
            }
        } else {
            String val = jedis.get(name);
            if(seconds > 0) {
                jedis.exists(name);
            }

            return val;
        }
    }

    public static Object getHParam(String name, String key, Jedis jedis) throws Exception {
        return getHParam(name, key, jedis, 0);
    }

    public static Object getHParam(String name, String key, Jedis jedis, int seconds) throws Exception {
        String type = jedis.hget("param_type", name + "##" + key);
        if("n".equals(type)) {
            return null;
        } else if("b".equals(type)) {
            byte[] val2 = jedis.hget(name.getBytes(), key.getBytes());
            Object val1 = Utils.convertBytes2Obj(val2);
            if(seconds > 0) {
                jedis.expire(name.getBytes(), seconds);
            }

            return val1;
        } else {
            String val = jedis.hget(name, key);
            if(seconds > 0) {
                jedis.expire(name, seconds);
            }

            return val;
        }
    }

    public static Object getHParam(String name, String key) throws Exception {
        Jedis jedis = null;

        Object var5;
        try {
            jedis = getConnection();
            Object e = getHParam(name, key, jedis);
            var5 = e;
        } catch (Exception var8) {
            throw var8;
        } finally {
            freeConnection(jedis);
        }

        return var5;
    }
}
