package com.core.server.wxtools;

import com.core.server.log.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class GetWxOrderno {
    public static DefaultHttpClient httpclient = new DefaultHttpClient();

    static {
        httpclient = (DefaultHttpClient)HttpClientConnectionManager.getSSLInstance(httpclient);
    }

    public GetWxOrderno() {
    }

    public static String getPayNo(String url, String xmlParam) {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.valueOf(true));
        HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
        String prepay_id = "";

        try {
            httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
            CloseableHttpResponse e = httpclient.execute(httpost);
            String jsonStr = EntityUtils.toString(e.getEntity(), "UTF-8");
            Logger.error(jsonStr);
            new HashMap();
            if(jsonStr.indexOf("FAIL") != -1) {
                return prepay_id;
            }

            if(jsonStr != null || !"".equals(jsonStr)) {
                try {
                    Document e1 = DocumentHelper.parseText(jsonStr);
                    prepay_id = e1.valueOf("/xml/prepay_id/text()");
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return prepay_id;
    }

    public static String getcode_url(String url, String xmlParam) {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.valueOf(true));
        HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
        String code_url = "";

        try {
            httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
            CloseableHttpResponse e = httpclient.execute(httpost);
            String jsonStr = EntityUtils.toString(e.getEntity(), "UTF-8");
            new HashMap();
            if(jsonStr.indexOf("FAIL") != -1) {
                return code_url;
            }

            if(jsonStr != null || !"".equals(jsonStr)) {
                try {
                    Document e1 = DocumentHelper.parseText(jsonStr);
                    code_url = e1.valueOf("/xml/code_url/text()");
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return code_url;
    }

    public static String https_request(String url, String xmlParam) {
        String jsonStr = "";
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.valueOf(true));
        HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);

        try {
            httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
            CloseableHttpResponse e = httpclient.execute(httpost);
            jsonStr = EntityUtils.toString(e.getEntity(), "UTF-8");
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return jsonStr;
    }

    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();

            while(it.hasNext()) {
                Element e = (Element)it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }

                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    public static InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}
