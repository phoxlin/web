package com.core.server.wx;

import com.core.server.log.Logger;
import org.json.JSONObject;

import java.util.TreeMap;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class WXPayToPay {
    private String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public WXPayToPay() {
    }

    public static void main(String[] args) throws NumberFormatException, Exception {
        WXPayToPay wx = new WXPayToPay();
        wx.Topay(Float.valueOf(Float.parseFloat("1")), "xxx", "oOBFQwKIx3xnJx-Jx1YZPu_wRUiA", "1dwewq23", "wxdd743d586df74621", "c3ecdd5ab7fb96cb132ae0b934fc683f", "1409220302", "5qir9qTNSr3GpD7awh2aQdLzZFJ1SoVR");
    }

    public JSONObject Topay(Float money, String subject, String openId, String orderNo, String appid, String appsecret, String partner, String partnerkey) throws Exception {
        Logger.error("openId----------------->" + openId);
        Logger.error("appid----------------->" + appid);
        Logger.error("appsecret----------------->" + appsecret);
        Logger.error("partner----------------->" + partner);
        Logger.error("partnerkey----------------->" + partnerkey);
        String finalmoney = String.valueOf(money.intValue());
        String nonce_str = CreateNonce_str.createNonce_str();
        String attach = "";
        String spbill_create_ip = GetRealIp.getRealIp().toString();
        String notify_url = "http://yeapao.com/app/notifys_url.jsp";
        String trade_type = "JSAPI";
        TreeMap packageParams = new TreeMap();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", partner);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("body", subject);
        packageParams.put("attach", attach);
        packageParams.put("out_trade_no", orderNo);
        packageParams.put("total_fee", finalmoney);
        packageParams.put("spbill_create_ip", spbill_create_ip);
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);
        packageParams.put("openid", openId);
        RequestHandler reqHandler = new RequestHandler();
        reqHandler.init(appid, appsecret, partnerkey);
        String sign = reqHandler.createSign(packageParams);
        String xml = "<xml><appid>" + appid + "</appid>" + "<mch_id>" + partner + "</mch_id>" + "<nonce_str>" + nonce_str + "</nonce_str>" + "<sign>" + sign + "</sign>" + "<body><![CDATA[" + subject + "]]></body>" + "<attach>" + attach + "</attach>" + "<out_trade_no>" + orderNo + "</out_trade_no>" + "<total_fee>" + finalmoney + "</total_fee>" + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" + "<notify_url>" + notify_url + "</notify_url>" + "<trade_type>" + trade_type + "</trade_type>" + "<openid>" + openId + "</openid>" + "</xml>";
        String prepay_id = "";

        try {
            prepay_id = GetWxOrderno.getPayNo(this.createOrderURL, xml);
            if("".equals(prepay_id)) {
                Logger.error("统一支付接口获取预支付订单出错");
            }
        } catch (Exception var29) {
            Logger.error(var29);
        }

        JSONObject obj = new JSONObject();
        if(!"".equals(prepay_id)) {
            TreeMap finalpackage = new TreeMap();
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String packages = "prepay_id=" + prepay_id;
            finalpackage.put("appId", appid);
            finalpackage.put("timeStamp", timestamp);
            finalpackage.put("nonceStr", nonce_str);
            finalpackage.put("package", packages);
            finalpackage.put("signType", "MD5");
            String finalsign = reqHandler.createSign(finalpackage);
            obj.put("appid", appid);
            obj.put("timeStamp", timestamp);
            obj.put("nonceStr", nonce_str);
            obj.put("packages", packages);
            obj.put("sign", finalsign);
        }

        return obj;
    }
}
