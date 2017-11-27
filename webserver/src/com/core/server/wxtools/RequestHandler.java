package com.core.server.wxtools;

import com.core.server.wx.MD5Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class RequestHandler {
    private String tokenUrl;
    private String gateUrl;
    private String notifyUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";
    private String appid;
    private String appkey;
    private String partnerkey;
    private String appsecret;
    private String key;
    private SortedMap parameters = new TreeMap();
    private String Token;
    private String charset = "UTF-8";
    private String debugInfo;
    private String last_errcode = "0";

    public RequestHandler() {
    }

    public void init(String app_id, String app_secret, String partner_key) {
        this.last_errcode = "0";
        this.Token = "token_";
        this.debugInfo = "";
        this.appid = app_id;
        this.partnerkey = partner_key;
        this.appsecret = app_secret;
        this.key = partner_key;
    }

    public void init() {
    }

    public String getLasterrCode() {
        return this.last_errcode;
    }

    public String getGateUrl() {
        return this.gateUrl;
    }

    public String getParameter(String parameter) {
        String s = (String)this.parameters.get(parameter);
        return s == null?"":s;
    }

    public void setKey(String key) {
        this.partnerkey = key;
    }

    public void setAppKey(String key) {
        this.appkey = key;
    }

    public String UrlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, this.charset).replace("+", "%20");
    }

    public String genPackage(SortedMap<String, String> packageParams) throws UnsupportedEncodingException {
        String sign = this.createSign(packageParams);
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();

        while(it.hasNext()) {
            Map.Entry packageValue = (Map.Entry)it.next();
            String k = (String)packageValue.getKey();
            String v = (String)packageValue.getValue();
            sb.append(k + "=" + this.UrlEncode(v) + "&");
        }

        String packageValue1 = sb.append("sign=" + sign).toString();
        return packageValue1;
    }

    public String createSign(SortedMap<String, String> packageParams) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();

        while(it.hasNext()) {
            Map.Entry sign = (Map.Entry)it.next();
            String k = (String)sign.getKey();
            String v = (String)sign.getValue();
            if(v != null && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + this.getKey());
        System.out.println("md5 sb:" + sb + "key=" + this.getKey());
        String sign1 = MD5Util.MD5Encode(sb.toString(), this.charset).toUpperCase();
        System.out.println("packge签名:" + sign1);
        return sign1;
    }

    public boolean createMd5Sign(String signParams) {
        StringBuffer sb = new StringBuffer();
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();

        String sign;
        String tenpaySign;
        while(it.hasNext()) {
            Map.Entry enc = (Map.Entry)it.next();
            sign = (String)enc.getKey();
            tenpaySign = (String)enc.getValue();
            if(!"sign".equals(sign) && tenpaySign != null && !"".equals(tenpaySign)) {
                sb.append(sign + "=" + tenpaySign + "&");
            }
        }

        String enc1 = "utf-8";
        sign = MD5Util.MD5Encode(sb.toString(), enc1).toLowerCase();
        tenpaySign = this.getParameter("sign").toLowerCase();
        this.setDebugInfo(sb.toString() + " => sign:" + sign + " tenpaySign:" + tenpaySign);
        return tenpaySign.equals(sign);
    }

    public String parseXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();

        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(v != null && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("<" + k + ">" + this.getParameter(k) + "</" + k + ">\n");
            }
        }

        sb.append("</xml>");
        return sb.toString();
    }

    protected void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public void setPartnerkey(String partnerkey) {
        this.partnerkey = partnerkey;
    }

    public String getDebugInfo() {
        return this.debugInfo;
    }

    public String getKey() {
        return this.partnerkey;
    }
}
