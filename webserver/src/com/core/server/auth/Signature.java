package com.core.server.auth;

import com.core.server.db.DBUtils;
import com.core.server.db.xml.Data;
import com.core.server.db.xml.Datas;

import java.security.MessageDigest;
import java.util.Formatter;

public class Signature {
    private String appId;
    private String appSecret;
    private String nonce_str;
    private String timestamp;
    private String signature;

    public Signature(String appId, String appSecret, String nonce_str, String timestamp) throws Exception {
        Data d = Datas.findOne("sys_apps", "appId", "=", appId);
        if(d == null) {
            throw new Exception("系统找不到：AppId【" + appId + "】的项目");
        } else {
            String string1 = "appId=" + appId + "&appSecret=" + appSecret + "&noncestr=" + nonce_str + "&timestamp=" + timestamp;
            this.appId = appId;
            this.appSecret = appSecret;
            this.nonce_str = nonce_str;
            this.timestamp = timestamp;
            this.signature = getSignature(string1);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getSignature(DBUtils.uuid() + DBUtils.uuid() + DBUtils.uuid() + DBUtils.uuid() + DBUtils.uuid()));
    }

    public static String getSignature(String str) throws Exception {
        if(str == null) {
            str = DBUtils.oid();
        }

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(str.getBytes("UTF-8"));
        String signature = byteToHex(crypt.digest());
        return signature;
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

    public String getAppId() {
        return this.appId;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public String getNonce_str() {
        return this.nonce_str;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getSignature() {
        return this.signature;
    }
}
