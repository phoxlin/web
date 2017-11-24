package com.core.server.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/11/24.
 */
public class DelayedItem<T> implements Delayed {
    private T t;
    private long removeTime;

    public DelayedItem(T t, long liveTime) {
        this.setT(t);
        this.removeTime = TimeUnit.NANOSECONDS.convert(liveTime * 1000L * 1000L * 1000L, TimeUnit.NANOSECONDS) + System.nanoTime();
    }

    public int compareTo(Delayed o) {
        if(o == null) {
            return 1;
        } else if(o == this) {
            return 0;
        } else if(o instanceof DelayedItem) {
            DelayedItem diff1 = (DelayedItem)o;
            return this.removeTime > diff1.removeTime?1:(this.removeTime == diff1.removeTime?0:-1);
        } else {
            long diff = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
            return diff > 0L?1:(diff == 0L?0:-1);
        }
    }

    public long getDelay(TimeUnit unit) {
        return unit.convert(this.removeTime - System.nanoTime(), unit);
    }

    public T getT() {
        return this.t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int hashCode() {
        return this.t.hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof DelayedItem?object.hashCode() == this.hashCode():false;
    }
}
