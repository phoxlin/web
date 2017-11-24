package com.core.server.tools;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PhoneMsgUtils {
    public static final String platform = Resources.getProperty("PHONE_MSG_PLATFORM", "中国网建");
    public static final String url = Resources.getProperty("PHONE_MSG_URL", "http://utf8.sms.webchinese.cn");
    public static final String name = Resources.getProperty("PHONE_MSG_NAME", "mingsokj.com");
    public static final String pwd = Resources.getProperty("PHONE_MSG_PWD", "2e228cca1ec44ea1c063");
    public static final String PHONE_MSG_SIGNATURE = Resources.getProperty("PHONE_MSG_SIGNATURE");
    public static final String pwd2;
    private static final HttpClient client;

    static {
        pwd2 = Utils.getBase64(pwd.getBytes());
        client = new HttpClient();
    }

    public PhoneMsgUtils() {
    }

    public static void main(String[] args) throws Exception {
        PhoneMessage msg = new PhoneMessage();
        msg.setContent("你好，请在今天下午2点 准时到公司面试带份简历。地址：双流白家镇黄河南路3段69号，电话：18008066220 郭");
        msg.setPhoneNumbers("18008066220");
        msg.sendMessage(true);
    }

    public static void sendMsg(String content, String phoneNumbers) throws Exception {
        if(content == null) {
            content = "";
        }

        if(PHONE_MSG_SIGNATURE != null && PHONE_MSG_SIGNATURE.length() > 0) {
            content = content + PHONE_MSG_SIGNATURE;
        }

        String result = null;

        try {
            PostMethod e = new PostMethod(url);
            e.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            NameValuePair[] data = new NameValuePair[]{new NameValuePair("Uid", name), new NameValuePair("Key", pwd), new NameValuePair("smsMob", phoneNumbers), new NameValuePair("smsText", content)};
            e.setRequestBody(data);
            client.executeMethod(e);
            String num = new String(e.getResponseBodyAsString().getBytes("utf-8"));
            switch(num.hashCode()) {
                case 1444:
                    if(num.equals("-1")) {
                        result = "没有该用户账户";
                    }
                    break;
                case 1445:
                    if(num.equals("-2")) {
                        result = "密钥不正确";
                    }
                    break;
                case 1446:
                    if(num.equals("-3")) {
                        result = "短信数量不足";
                    }
                    break;
                case 1447:
                    if(num.equals("-4")) {
                        result = "手机号格式不正确";
                    }
                    break;
                case 44813:
                    if(num.equals("-11")) {
                        result = "该用户被禁用";
                    }
                    break;
                case 44816:
                    if(num.equals("-14")) {
                        result = "短信内容出现非法字符";
                    }
                    break;
                case 44906:
                    if(num.equals("-41")) {
                        result = "手机号码为空";
                    }
                    break;
                case 44907:
                    if(num.equals("-42")) {
                        result = "短信内容为空";
                    }
                    break;
                case 44937:
                    if(num.equals("-51")) {
                        result = "短信签名格式不正确接口签名格式为：【签名内容】";
                    }
            }

            e.releaseConnection();
            if(result != null) {
                throw new Exception(result);
            } else {
                Logger.info("PhoneMsgUtils really sended a Phone Msg to :" + phoneNumbers);
            }
        } catch (Exception var7) {
            throw var7;
        }
    }
}
