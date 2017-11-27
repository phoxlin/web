package com.core.server.wx;

import com.core.server.db.DBUtils;
import com.core.server.log.JhLog;
import com.core.server.tools.RedisUtils;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;
import com.core.server.upload.QiniuAction;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class Wx {
    private String appid;
    private String appsecret;
    private String nonceStr;
    private String timestamp;
    private String signature;
    private String openId;
    private String nickname;
    private String sex;
    private String headUrl;
    private JhLog L;

    public Wx(JhLog L) throws Exception {
        if(L == null) {
            L = new JhLog();
        }

        this.L = L;
        String appid = Resources.getProperty("wx.appid");
        String appsecret = Resources.getProperty("wx.appsecret");
        if(appid != null && appid.length() > 0) {
            if(appsecret != null && appsecret.length() > 0) {
                this.appid = appid;
                this.appsecret = appsecret;
            } else {
                throw new Exception("系统没有配置【wx.appsecret】");
            }
        } else {
            throw new Exception("系统没有配置【wx.appid】");
        }
    }

    public Wx(String appid, String appsecret, JhLog L) throws Exception {
        if(L == null) {
            L = new JhLog();
        }

        this.L = L;
        this.appid = appid;
        this.appsecret = appsecret;
        if(this.appid != null && this.appid.length() > 0) {
            if(this.appsecret == null || this.appsecret.length() <= 0) {
                throw new Exception("系统没有配置【wx.appsecret】");
            }
        } else {
            throw new Exception("系统没有配置【wx.appid】");
        }
    }

    public void config(String url) throws Exception {
        String js_ticket = this.getWebAccessToken();
        Map ret = WxSign.sign(js_ticket, url);
        this.nonceStr = Utils.getMapStringValue(ret, "nonceStr");
        this.timestamp = Utils.getMapStringValue(ret, "timestamp");
        this.signature = Utils.getMapStringValue(ret, "signature");
    }

    public String getWebAccessToken() throws Exception {
        Jedis jedis = null;
        String ticket = null;

        try {
            jedis = RedisUtils.getConnection();
            Object e = RedisUtils.getHParam("weixin", this.appid + "_" + this.appsecret + "_webticket", jedis);
            if(e != null && e.toString().length() > 0) {
                ticket = e.toString();
            } else {
                String jsrequestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + this.getAccessToken() + "&type=jsapi";
                JSONObject js = this.httpRequest(jsrequestUrl, "GET", (String)null);
                if(!js.keySet().contains("ticket")) {
                    throw new Exception("获取web_ticket接口失败");
                }

                ticket = js.getString("ticket");
                RedisUtils.setHParam("weixin", this.appid + "_" + this.appsecret + "_webticket", ticket, jedis, 1800);
            }
        } catch (Exception var9) {
            throw var9;
        } finally {
            RedisUtils.freeConnection(jedis);
        }

        return ticket;
    }

    public String getAccessToken() throws Exception {
        Jedis jedis = null;
        String at = null;

        try {
            jedis = RedisUtils.getConnection();
            Object e = RedisUtils.getHParam("weixin", this.appid + "_" + this.appsecret, jedis);
            if(e != null && e.toString().length() > 0) {
                at = e.toString();
            } else {
                String web_access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + this.appid + "&secret=" + this.appsecret;
                JSONObject obj2 = this.httpRequest(web_access_token_url, "GET", (String)null);
                if(!obj2.keySet().contains("access_token")) {
                    throw new Exception("获取access_token接口失败");
                }

                at = obj2.getString("access_token");
                RedisUtils.setHParam("weixin", this.appid + "_" + this.appsecret, at, jedis, 1800);
            }
        } catch (Exception var9) {
            throw var9;
        } finally {
            RedisUtils.freeConnection(jedis);
        }

        return at;
    }

    public String getAccessToken(boolean refeash) throws Exception {
        if(refeash) {
            Jedis jedis = null;
            String at = null;

            try {
                jedis = RedisUtils.getConnection();
                String e = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + this.appid + "&secret=" + this.appsecret;
                JSONObject obj2 = this.httpRequest(e, "GET", (String)null);
                if(!obj2.keySet().contains("access_token")) {
                    throw new Exception("获取access_token接口失败");
                }

                at = obj2.getString("access_token");
                RedisUtils.setHParam("weixin", this.appid + "_" + this.appsecret, at, jedis, 1800);
            } catch (Exception var9) {
                throw var9;
            } finally {
                RedisUtils.freeConnection(jedis);
            }

            return at;
        } else {
            return this.getAccessToken();
        }
    }

    public String getOpenId(String code, boolean refresh) throws Exception {
        if(this.openId == null || this.openId.length() <= 0 || refresh) {
            Jedis e = null;

            try {
                e = RedisUtils.getConnection();
                Object obj2 = RedisUtils.getHParam("weixin", this.appid + "_" + this.appsecret + "_" + code, e);
                if(obj2 != null && obj2.toString().length() > 0) {
                    this.openId = obj2.toString();
                } else {
                    String web_access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + this.appid + "&secret=" + this.appsecret + "&code=" + code + "&grant_type=authorization_code";
                    JSONObject obj21 = this.httpRequest(web_access_token_url, "GET", (String)null);
                    boolean isCon = obj21.keySet().contains("openid");
                    if(obj21 == null || !isCon) {
                        throw new Exception("获取openid接口失败");
                    }

                    this.openId = obj21.getString("openid");
                    RedisUtils.setHParam("weixin", this.appid + "_" + this.appsecret + "_" + code, this.openId, e, 1800);
                }
            } catch (Exception var13) {
                throw var13;
            } finally {
                RedisUtils.freeConnection(e);
            }
        }

        try {
            String e1 = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + this.getAccessToken() + "&openid=" + this.getOpenId() + "&lang=zh_CN";
            JSONObject obj22 = this.httpRequest(e1, "GET", (String)null);
            if(obj22 != null && obj22.keySet().contains("nickname")) {
                this.nickname = obj22.getString("nickname");
                this.sex = "1".equals(obj22.getString("sex"))?"male":"female";
                this.headUrl = obj22.getString("headimgurl");
            } else {
                e1 = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + this.getAccessToken(true) + "&openid=" + this.getOpenId() + "&lang=zh_CN";
                obj22 = this.httpRequest(e1, "GET", (String)null);
                if(obj22 == null || !obj22.keySet().contains("nickname")) {
                    throw new Exception("获取openid接口失败");
                }

                this.nickname = obj22.getString("nickname");
                if(this.nickname == null || this.nickname.trim().length() <= 0) {
                    this.nickname = "N/A";
                }

                this.sex = "1".equals(obj22.getString("sex"))?"male":"female";
                this.headUrl = obj22.getString("headimgurl");
            }
        } catch (Exception var12) {
            this.L.error(var12);
        }

        return this.openId;
    }

    private JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        TrustManager[] tm = new TrustManager[]{new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init((KeyManager[])null, tm, new SecureRandom());
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        this.L.debug("wx get url:" + requestUrl);
        HttpsURLConnection httpUrlConn = (HttpsURLConnection)url.openConnection();
        httpUrlConn.setSSLSocketFactory(ssf);
        httpUrlConn.setDoOutput(true);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        httpUrlConn.setRequestMethod(requestMethod);
        if("GET".equalsIgnoreCase(requestMethod)) {
            httpUrlConn.connect();
        }

        OutputStream inputStream;
        if(outputStr != null) {
            inputStream = httpUrlConn.getOutputStream();
            inputStream.write(outputStr.getBytes("UTF-8"));
            inputStream.close();
        }

        InputStream inputStream1 = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream1, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;

        while((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }

        bufferedReader.close();
        inputStreamReader.close();
        inputStream1.close();
        inputStream = null;
        httpUrlConn.disconnect();
        this.L.debug(buffer.toString());
        jsonObject = new JSONObject(buffer.toString());
        return jsonObject;
    }

    public String downMedia(String mediaId) throws Exception {
        String access_token = this.getAccessToken();
        String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + access_token + "&media_id=" + mediaId;
        this.L.info(requestUrl);
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        HttpURLConnection conn = null;

        try {
            URL e = new URL(requestUrl);
            conn = (HttpURLConnection)e.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
            String filename = DBUtils.uuid();
            String filepath = Utils.getFilePath(filename);
            File f = new File(basePath + filepath);
            if(!f.exists()) {
                f.mkdirs();
            }

            this.L.info("<-----------filepath:" + basePath + filepath);
            String contentType = conn.getHeaderField("Content-Type");
            this.L.error("<---------contentType:" + contentType);
            String ext = "jpg";
            String fileName;
            if(contentType.startsWith("image/jpeg")) {
                ext = "jpg";
            } else if(contentType.startsWith("image/png")) {
                ext = "png";
            } else {
                fileName = HtmlUtils.get(requestUrl);

                try {
                    JSONObject file = new JSONObject(fileName);
                    int buf = file.getInt("errcode");
                    if(buf != '鱁') {
                        return null;
                    }

                    this.L.info("发现下载文件的类型有问题， 重新刷新accessToken，重新下载文件");
                    Thread.sleep(1000L);
                } catch (Exception var36) {
                    this.L.error(var36);
                    return null;
                }
            }

            this.L.info("contentType:" + contentType);
            fileName = filename + "." + ext;
            File file1 = new File(basePath + filepath + "/" + fileName);
            bis = new BufferedInputStream(conn.getInputStream());
            fos = new FileOutputStream(file1);
            byte[] buf1 = new byte[8096];
            boolean size = false;

            int size1;
            while((size1 = bis.read(buf1)) != -1) {
                fos.write(buf1, 0, size1);
            }

            String qiniu_url = QiniuAction.upload(file1);
            file1.delete();
            String info = String.format("下载媒体文件成功，filePath=" + qiniu_url, new Object[0]);
            this.L.info(info);
            String var21 = qiniu_url;
            return var21;
        } catch (Exception var37) {
            throw var37;
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException var35) {
                    ;
                }
            }

            if(bis != null) {
                try {
                    bis.close();
                } catch (IOException var34) {
                    ;
                }
            }

            if(conn != null) {
                conn.disconnect();
            }

        }
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return this.appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getNonceStr() {
        return this.nonceStr;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getOpenId() {
        return this.openId;
    }

    public String getSex() {
        return this.sex;
    }

    public String getHeadUrl() {
        return this.headUrl;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
