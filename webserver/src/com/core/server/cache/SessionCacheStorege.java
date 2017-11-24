package com.core.server.cache;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * Created by Administrator on 2017/11/24.
 */
public class SessionCacheStorege {
    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap();
    private DelayQueue<DelayedItem<String>> queue = new DelayQueue();

    public static SessionCacheStorege getInstance() {
        SessionCacheStorege s = SessionCacheStorege.SingletonHolder.instance;
        Logger.info("System session time set to:" + SystemUtils.sessionTime + " seconds.");
        return s;
    }

    public Object get(String k) throws Exception {
        Object v2 = this.map.get(k);
        return v2 instanceof byte[]?Utils.convertBytes2Obj((byte[])v2):(v2 != null?v2.toString():null);
    }

    public void expire(String k, long liveTime) throws Exception {
        Object v2 = this.map.get(k);
        DelayedItem tmpItem = new DelayedItem(k, liveTime);
        if(v2 != null) {
            this.queue.remove(tmpItem);
            Logger.debug("Changed 【" + k + "】 expire time to " + liveTime + " seconds later.");
            this.queue.put(tmpItem);
        } else {
            throw new Exception("Can\'t find key【" + k + "】 from cache.");
        }
    }

    public void put(String k, Object v) throws Exception {
        Object v2 = null;
        if(!(v instanceof String) && !(v instanceof Integer) && !(v instanceof Float) && !(v instanceof Double) && !(v instanceof Logger)) {
            byte[] tmpItem = Utils.convertObj2Btyes(v);
            v2 = this.map.put(k, tmpItem);
        } else {
            v2 = this.map.put(k, "" + v);
        }

        DelayedItem tmpItem1 = new DelayedItem(k, (long)SystemUtils.sessionTime);
        if(v2 != null) {
            this.queue.remove(tmpItem1);
        }

        this.queue.put(tmpItem1);
    }

    public SessionCacheStorege() {
        Thread t = new Thread() {
            public void run() {
                SessionCacheStorege.this.dameonCheckOverdueKey();
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void dameonCheckOverdueKey() {
        while(true) {
            DelayedItem delayedItem = (DelayedItem)this.queue.poll();
            if(delayedItem != null) {
                this.map.remove(delayedItem.getT());
                Logger.debug("Removed 【" + (String)delayedItem.getT() + "】 from cache");
            }

            try {
                Thread.sleep(300L);
            } catch (Exception var3) {
                ;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        byte cacheNumber = 10;
        BasicSessionCache cache = new BasicSessionCache();

        for(int i = 0; i < cacheNumber; ++i) {
            cache.setParam(i + "k", "v" + i);
        }

        Thread.sleep(5000L);
        cache.expire("5k", 6L);
        Thread.sleep(10000000L);
        System.out.println();
    }

    public Enumeration<String> keys() {
        return this.map.keys();
    }

    private static class SingletonHolder {
        static SessionCacheStorege instance = new SessionCacheStorege();

        private SingletonHolder() {
        }
    }
}
