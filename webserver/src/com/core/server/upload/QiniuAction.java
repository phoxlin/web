package com.core.server.upload;

import com.core.SFile;
import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.db.DBUtils;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.tools.PicUtils;
import com.core.server.tools.Resources;
import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QiniuAction extends BasicAction {
    private static String accessKey = Resources.getProperty("qiniu.filesotre.accessKey");
    private static String secretKey = Resources.getProperty("qiniu.filesotre.secretKey");
    private static String bucketName = Resources.getProperty("qiniu.filesotre.bucketName");
    public static boolean configV1 = Resources.getBooleanProperty("qiniu.filesotre.v1");
    private static String bucketUrl = Resources.getProperty("qiniu.filesotre.bucketUrl", "http://oixty02vf.bkt.clouddn.com/");



    @Route(value = "/upload-qiniu-uptoken", conn = false, m = {HttpMethod.GET}, type = ContentType.JSON)
    public void uptoken() throws Exception {
        Auth auth = Auth.create(accessKey, secretKey);
        String key = DBUtils.oid();
        String token = auth.uploadToken(bucketName);
        this.obj.put("key", key);
        this.obj.put("uptoken", token);
    }

    @Route(value = "/getUploadPic/fid", conn = false, m = {HttpMethod.GET}, type = ContentType.JPG)
    public void getUploadPic(String fid) throws Exception {
        SFile file = null;
        if(fid != null) {
            if(fid.length() == 32) {
                file = new SFile(fid);
            } else if(fid.length() == 40) {
                file = SFile.createSFileByHashCode(fid);
            }
        }

        if(file == null) {
            throw new Exception("没有找到系统文件");
        } else {
            Set keys = this.request.getParameterMap().keySet();
            File des = null;
            if(file.isPic()) {
                int reader = 0;
                int out = 0;
                this.response.setContentType("image/jpeg; charset=UTF-8");
                Iterator len = keys.iterator();

                while(len.hasNext()) {
                    String bytes = (String)len.next();
                    if(bytes.startsWith("imageView2")) {
                        String[] e = bytes.split("/");
                        if(e != null && e.length > 0) {
                            for(int i = 0; i < e.length; ++i) {
                                if("w".equalsIgnoreCase(e[i])) {
                                    if(e.length > i + 1) {
                                        try {
                                            reader = Integer.parseInt(e[i + 1]);
                                        } catch (Exception var17) {
                                            ;
                                        }
                                    }
                                } else if("h".equalsIgnoreCase(e[i]) && e.length > i + 1) {
                                    try {
                                        out = Integer.parseInt(e[i + 1]);
                                    } catch (Exception var16) {
                                        ;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

                if(reader > 0 && out > 0) {
                    des = new File(file.getPath() + "." + reader + "_" + out + "." + file.getExt());
                    if(!des.exists()) {
                        PicUtils.compressPic(file.getFile(), des, reader, out);
                    }
                } else {
                    des = file.getFile();
                }
            } else {
                des = file.getFile();
            }

            this.response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
            FileInputStream var20 = null;
            ServletOutputStream var21 = null;
            byte[] var22 = new byte[1024];
            boolean var23 = false;

            try {
                var20 = new FileInputStream(des);
                var21 = this.response.getOutputStream();

                int var24;
                while((var24 = var20.read(var22)) > 0) {
                    var21.write(var22, 0, var24);
                }
            } catch (Exception var18) {
                var18.printStackTrace();
            } finally {
                if(var20 != null) {
                    var20.close();
                }

                if(var21 != null) {
                    var21.close();
                }

            }

        }
    }

    public static void main(String[] args) throws QiniuException {
        File f = new File("D:/log4j.log");
        String url = upload(f);
        System.out.println(url);
    }

    public static String upload(File filePath) throws QiniuException {
        try {
            if(configV1) {
                Config.zone = Zone.zone1();
            }

            Auth e = Auth.create(accessKey, secretKey);
            String key = DBUtils.oid() + "_._" + filePath.getName();
            String token = e.uploadToken(bucketName);
            UploadManager uploadManager = new UploadManager();
            uploadManager.put(filePath, key, token);
            return bucketUrl + key;
        } catch (QiniuException var5) {
            throw var5;
        }
    }
}
