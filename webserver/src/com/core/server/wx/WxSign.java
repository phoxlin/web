package com.core.server.wx;

import com.core.server.log.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class WxSign {
    public static String createLinkString(Map<String, String> params) {
        ArrayList keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        String prestr = "";

        for(int i = 0; i < keys.size(); ++i) {
            String key = (String)keys.get(i);
            String value = (String)params.get(key);
            if(i == keys.size() - 1) {
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    public static Map<String, String> sign(String jsapi_ticket, String url) {
        HashMap ret = new HashMap();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String signature = "";
        String string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        Logger.error("string1----------" + string1);

        try {
            MessageDigest e = MessageDigest.getInstance("SHA-1");
            e.reset();
            e.update(string1.getBytes("UTF-8"));
            signature = byteToHex(e.digest());
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        } catch (UnsupportedEncodingException var9) {
            var9.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }

    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        byte[] var5 = hash;
        int var4 = hash.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte result = var5[var3];
            formatter.format("%02x", new Object[]{Byte.valueOf(result)});
        }

        String var6 = formatter.toString();
        formatter.close();
        return var6;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }
}
