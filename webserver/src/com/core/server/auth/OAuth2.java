package com.core.server.auth;

import com.core.server.Action;
import com.core.server.db.DBUtils;
import com.core.server.db.xml.Data;
import com.core.server.db.xml.Datas;
import com.core.server.log.Logger;

public class OAuth2 {
    public OAuth2() throws Exception {
    }

    public void createApp() throws Exception {
        System.out.println("随机产生一个项目的ID和密码：");
        System.out.println("AppId： jh_" + DBUtils.oid());
        System.out.println("AppSecret： " + Signature.getSignature("jh_" + DBUtils.oid() + "_" + System.currentTimeMillis()));
    }

    public static void main(String[] args) throws Exception {
        (new OAuth2()).createApp();
    }

    public Signature sign(String appId, String appSecret, String nonce_str, String timestamp) throws Exception {
        if(nonce_str == null || nonce_str.length() <= 32) {
            nonce_str = DBUtils.uuid();
        }

        if(timestamp == null || timestamp.length() <= 5) {
            timestamp = Long.toString(System.currentTimeMillis() / 1000L);
        }

        Signature sign = new Signature(appId, appSecret, nonce_str, timestamp);
        return sign;
    }

    public String accessToken(String appId, String signature, String timestamp, String nonceStr, Action act) throws Exception {
        try {
            this.getSignature(appId, signature, nonceStr, timestamp);
            return DBUtils.oid();
        } catch (Exception var7) {
            throw var7;
        }
    }

    public Signature getSignature(String appId, String signature, String nonceStr, String timestamp) throws Exception {
        Data d = Datas.findOne("sys_apps", "appId", "=", appId);
        if(d == null) {
            throw new Exception("系统找不到：AppId【" + appId + "】的项目");
        } else {
            Signature sign = new Signature(appId, d.getStringValue("appSecret"), nonceStr, timestamp);
            if(!sign.getSignature().equals(signature)) {
                Logger.info("sign1:" + sign.getSignature());
                Logger.info("sign2:" + signature);
                throw new Exception("签名验证失败");
            } else {
                return sign;
            }
        }
    }
}
