package com.core.server.tools;

import com.core.server.log.Logger;
import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QiniuUtils {
    private static String accessKey = Resources.getProperty("qiniu.filesotre.accessKey");
    private static String secretKey = Resources.getProperty("qiniu.filesotre.secretKey");
    private static String bucketName = Resources.getProperty("qiniu.filesotre.bucketName");
    public static boolean configV1 = Resources.getBooleanProperty("qiniu.filesotre.v1");
    public static String bucketUrl = Resources.getProperty("qiniu.filesotre.bucketUrl", "http://oixty02vf.bkt.clouddn.com/");
    private static boolean qiniu = Resources.getProperty("yun.filestore.type").equalsIgnoreCase("qiniu");



    public static String upload(byte[] file, String fileName) throws Exception {
        if(qiniu) {
            try {
                if(configV1) {
                    Config.zone = Zone.zone1();
                }

                Auth e = Auth.create(accessKey, secretKey);
                String key = UUID.randomUUID().toString() + "_._" + fileName;
                String token = e.uploadToken(bucketName);
                UploadManager uploadManager = new UploadManager();
                uploadManager.put(file, key, token);
                return bucketUrl + key;
            } catch (QiniuException var6) {
                throw var6;
            }
        } else {
            return submitPost(file);
        }
    }

    public static String upload(File file) throws Exception {
        FileInputStream br = null;
        byte[] temps = new byte[(int)file.length()];

        String var5;
        try {
            br = new FileInputStream(file);
            br.read(temps, 0, (int)file.length());
            var5 = upload(temps, file.getName());
        } catch (Exception var12) {
            throw var12;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (Exception var11) {
                    ;
                }

                br = null;
            }

        }

        return var5;
    }

    public static String submitPost(byte[] file) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        File f = File.createTempFile("upload", ".jpg");

        String var13;
        try {
            String url = Resources.baseHttp + "public/pub/file_upload/upload.jsp";
            HttpPost httppost = new HttpPost(url);
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(f);
                fos.write(file);
            } catch (Exception var27) {
                throw var27;
            } finally {
                fos.close();
            }

            FileBody bin = new FileBody(f);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).build();
            httppost.setEntity(reqEntity);
            Logger.info("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);

            try {
                Logger.info("----------------------------------------");
                Logger.info(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null) {
                    Logger.info("Response content length: " + resEntity.getContentLength() + ":" + resEntity);
                }

                String content = Resources.convertStreamToString(resEntity.getContent());
                EntityUtils.consume(resEntity);
                JSONObject o = new JSONObject(content);
                var13 = Resources.baseHttp + o.getString("key");
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
            if(f != null && f.exists()) {
                f.delete();
            }

        }

        return var13;
    }
}
