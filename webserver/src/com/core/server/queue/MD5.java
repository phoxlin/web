package com.core.server.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class MD5 {
    private static final Logger log = LoggerFactory.getLogger(MD5.class);
    private static char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static Map<Character, Integer> rDigits = new HashMap(16);
    private static MD5 me;
    private MessageDigest mHasher;
    private final ReentrantLock opLock = new ReentrantLock();

    static {
        for(int i = 0; i < digits.length; ++i) {
            rDigits.put(Character.valueOf(digits[i]), Integer.valueOf(i));
        }

        me = new MD5();
    }

    private MD5() {
        try {
            this.mHasher = MessageDigest.getInstance("md5");
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public static MD5 getInstance() {
        return me;
    }

    public String getMD5String(String content) {
        return this.bytes2string(this.hash((String)content));
    }

    public String getMD5String(byte[] content) {
        return this.bytes2string(this.hash((byte[])content));
    }

    public byte[] getMD5Bytes(byte[] content) {
        return this.hash((byte[])content);
    }

    public byte[] hash(String str) {
        this.opLock.lock();

        byte[] var4;
        try {
            byte[] e = this.mHasher.digest(str.getBytes("utf-8"));
            if(e == null || e.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }

            var4 = e;
        } catch (UnsupportedEncodingException var7) {
            throw new RuntimeException("unsupported utf-8 encoding", var7);
        } finally {
            this.opLock.unlock();
        }

        return var4;
    }

    public byte[] hash(byte[] data) {
        this.opLock.lock();

        byte[] var4;
        try {
            byte[] bt = this.mHasher.digest(data);
            if(bt == null || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }

            var4 = bt;
        } finally {
            this.opLock.unlock();
        }

        return var4;
    }

    public String bytes2string(byte[] bt) {
        int l = bt.length;
        char[] out = new char[l << 1];
        int i = 0;

        for(int j = 0; i < l; ++i) {
            out[j++] = digits[(240 & bt[i]) >>> 4];
            out[j++] = digits[15 & bt[i]];
        }

        if(log.isDebugEnabled()) {
            log.debug("[hash]" + new String(out));
        }

        return new String(out);
    }

    public byte[] string2bytes(String str) {
        if(str == null) {
            throw new NullPointerException("参数不能为空");
        } else if(str.length() != 32) {
            throw new IllegalArgumentException("字符串长度必须是32");
        } else {
            byte[] data = new byte[16];
            char[] chs = str.toCharArray();

            for(int i = 0; i < 16; ++i) {
                int h = ((Integer)rDigits.get(Character.valueOf(chs[i * 2]))).intValue();
                int l = ((Integer)rDigits.get(Character.valueOf(chs[i * 2 + 1]))).intValue();
                data[i] = (byte)((h & 15) << 4 | l & 15);
            }

            return data;
        }
    }
}
