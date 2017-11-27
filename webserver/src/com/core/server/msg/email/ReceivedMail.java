package com.core.server.msg.email;

import com.core.SFile;
import com.core.enuts.SFileType;
import com.core.server.db.DBUtils;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class ReceivedMail {
    private MimeMessage message;
    private String uid;
    private String messageId;
    private String subject;
    private Address fromAddr;
    private List<Address> to;
    private List<Address> cc;
    private List<Address> bcc;
    private StringBuilder bodytext = null;
    private StringBuilder bodyHtml = null;
    private String body;
    private List<SFile> appx = new ArrayList();
    private static String dateformat = "yyyy-MM-dd HH:mm:ss";
    private Date sendDate;
    private String sendDateStr;
    private static Pattern encodeStringPattern = Pattern.compile("=\\?(.+)\\?(B|Q)\\?(.+)\\?=", 34);
    private final String[] CHARTSET_HEADER = new String[]{"Subject", "From", "To", "Cc", "Delivered-To"};
    private String headCharset;
    private String bodyCharset;
    private String charset;
    private long size;
    private String bodyHashCode;
    private String hashId;
    private String fk_fileId;
    private Connection conn;
    private Statement st;

    public ReceivedMail(String uid, MimeMessage message, Connection conn, Statement st) {
        this.uid = uid;
        this.message = message;
        this.conn = conn;
        this.st = st;
        this.getCharset();
    }

    public static String trimMessageId(String uid) {
        uid = uid.replace("+", "___and___");
        uid = uid.replace("-", "___minus___");
        return uid;
    }

    public List<Address> getTo() {
        if(this.to == null) {
            this.to = this.getMailAddress("TO");
        }

        return this.to;
    }

    public long getSize() {
        return this.size;
    }

    public String getBodyHashCode() {
        return this.bodyHashCode;
    }

    public List<Address> getCc() {
        if(this.cc == null) {
            this.cc = this.getMailAddress("cc");
        }

        return this.cc;
    }

    public List<Address> getBcc() {
        if(this.bcc == null) {
            this.bcc = this.getMailAddress("bcc");
        }

        return this.bcc;
    }

    private List<Address> getMailAddress(String type) {
        ArrayList addrs = new ArrayList();

        try {
            String addtype = type.toUpperCase();
            InternetAddress[] address = null;
            if(addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
                if(addtype.equals("TO")) {
                    address = (InternetAddress[])this.message.getRecipients(Message.RecipientType.TO);
                } else if(addtype.equals("CC")) {
                    address = (InternetAddress[])this.message.getRecipients(Message.RecipientType.CC);
                } else {
                    address = (InternetAddress[])this.message.getRecipients(Message.RecipientType.BCC);
                }

                if(address != null && address.length > 0) {
                    for(int i = 0; i < address.length; ++i) {
                        String email = this.getHeadStringValue(address[i].getAddress());
                        String personal = this.getHeadStringValue(address[i].getPersonal());
                        addrs.add(new Address(personal, email));
                    }
                }
            }
        } catch (Exception var8) {
            ;
        }

        return addrs;
    }

    private String getHeadStringValue(String obj) {
        String val = "";

        try {
            if(obj != null) {
                if(obj.startsWith("=?")) {
                    String[] t = obj.split(" ");
                    if(t.length > 1) {
                        obj = t[0] + "?=";
                    }

                    val = MimeUtility.decodeText(obj);
                } else {
                    val = MimeUtility.decodeText(obj);
                    String t1;
                    if(this.headCharset == null) {
                        if(Utils.isGoodStr(val)) {
                            return val;
                        }

                        this.headCharset = "GBK";
                        t1 = new String(obj.getBytes("iso-8859-1"), this.headCharset);
                        if(Utils.isGoodStr(t1)) {
                            return t1;
                        }

                        this.headCharset = "UTF-8";
                        t1 = new String(obj.getBytes("iso-8859-1"), this.headCharset);
                        if(Utils.isGoodStr(t1)) {
                            return t1;
                        }

                        this.headCharset = "iso-8859-1";
                        t1 = new String(obj.getBytes("iso-8859-1"), this.headCharset);
                        if(Utils.isGoodStr(t1)) {
                            return t1;
                        }
                    } else {
                        t1 = new String(obj.getBytes("iso-8859-1"), this.headCharset);
                        if(Utils.isGoodStr(t1)) {
                            return t1;
                        }
                    }
                }
            }
        } catch (Exception var4) {
            ;
        }

        return val;
    }

    public String getSubject() {
        if(this.subject == null) {
            try {
                this.subject = this.getHeadStringValue(this.message.getSubject());
            } catch (MessagingException var2) {
                this.subject = "";
            }
        }

        return this.subject;
    }

    public MimeMessage getMessage() {
        return this.message;
    }

    public String getUid() {
        return this.uid;
    }

    public Address getFromAddr() {
        if(this.fromAddr == null) {
            try {
                InternetAddress[] e = (InternetAddress[])this.message.getFrom();
                if(e != null && e.length > 0) {
                    String from = this.getHeadStringValue(e[0].getAddress());
                    String personal = this.getHeadStringValue(e[0].getPersonal());
                    this.fromAddr = new Address(personal, from);
                } else {
                    this.fromAddr = new Address((String)null, (String)null);
                }
            } catch (Exception var4) {
                this.fromAddr = new Address((String)null, (String)null);
            }
        }

        return this.fromAddr;
    }

    public Date getSendDate() {
        if(this.sendDate == null) {
            try {
                this.sendDate = this.message.getSentDate();
            } catch (MessagingException var2) {
                ;
            }

            if(this.sendDate == null) {
                this.sendDate = new Date();
            }
        }

        return this.sendDate;
    }

    public String getSendDateStr() {
        if(this.sendDateStr == null) {
            SimpleDateFormat format = new SimpleDateFormat(dateformat);
            this.sendDateStr = format.format(this.getSendDate());
        }

        return this.sendDateStr;
    }

    public String getBody() {
        if(this.bodytext == null) {
            if(this.bodytext == null) {
                this.bodytext = new StringBuilder();
            }

            if(this.bodyHtml == null) {
                this.bodyHtml = new StringBuilder();
            }

            String e;
            try {
                e = this.getType(this.message.getContentType());
                Object ext = this.message.getContent();
                if(ext instanceof Multipart) {
                    this.parseContent((Multipart)ext);
                } else if(ext instanceof String) {
                    if(e.equals("text/plain")) {
                        this.bodytext.append(ext.toString());
                    } else {
                        this.bodyHtml.append(ext.toString());
                    }
                } else if(ext instanceof InputStream) {
                    try {
                        InputStream basePath = (InputStream)ext;
                        Session filepath = Session.getDefaultInstance(System.getProperties(), (Authenticator)null);
                        MimeMessage f = new MimeMessage(filepath, basePath);
                        Object file = f.getContent();
                        if(file != null) {
                            if(this.charset == null || this.charset.length() <= 0) {
                                this.charset = "GBK";
                            }

                            this.bodyHtml.append(new String(file.toString().getBytes("iso-8859-1"), this.charset));
                        }
                    } catch (Exception var26) {
                        ;
                    }
                } else {
                    Logger.warn("Unkown content type:" + ext.toString());
                    this.bodytext = new StringBuilder(ext.toString());
                }
            } catch (Exception var27) {
                ;
            }

            this.size = 0L;
            int var28 = 0;

            for(int var29 = this.appx.size(); var28 < var29; ++var28) {
                this.size += ((SFile)this.appx.get(var28)).getFileSize();
            }

            if(this.bodyHtml.length() > 0) {
                this.body = this.bodyHtml.toString();
            } else {
                this.body = this.bodytext.toString();
            }

            this.size += (long)this.body.length();

            try {
                e = this.getSendDateStr() + ".txt";
                this.hashId = DBUtils.uuid();
                String var30 = "txt";
                String var31 = Resources.getProperty("FileStore", Utils.getWebRootPath());
                String var32 = Utils.getFilePath(this.hashId);
                File var33 = new File(var31 + var32);
                if(!var33.exists()) {
                    var33.mkdirs();
                }

                File var34 = new File(var31 + var32 + "/" + this.hashId + "." + var30);
                BufferedWriter bw = null;

                try {
                    bw = new BufferedWriter(new FileWriter(var34));
                    bw.write(this.body);
                    bw.flush();
                } catch (Exception var23) {
                    ;
                } finally {
                    if(bw != null) {
                        bw.close();
                    }

                }

                this.bodyHashCode = Utils.getSha1(var34);
                String isPic = "N";
                int width = 0;
                int height = 0;
                boolean pic = Utils.isPicFile(var34);
                if(pic) {
                    isPic = "Y";
                    BufferedImage fileSize = ImageIO.read(var34);
                    width = (int)((double)fileSize.getWidth((ImageObserver)null));
                    height = (int)((double)fileSize.getHeight((ImageObserver)null));
                }

                long var35 = var34.length();
                EntityImpl en = new EntityImpl(this.conn);
                en.executeQuery("select id  from sys_hash a where a.hash_code=\'" + this.bodyHashCode + "\'");
                if(en.getResultCount() <= 0) {
                    String fileName2 = "insert into sys_hash(id,hash_code,filename,file_size,extension)VALUES(\'" + this.hashId + "\',\'" + this.bodyHashCode + "\',\'" + e + "\'," + var35 + ",\'" + var30 + "\')";
                    this.st.addBatch(fileName2);
                } else {
                    this.hashId = en.getStringValue("id");
                    var34.delete();
                }

                Date now = new Date();
                this.fk_fileId = DBUtils.uuid();
                String fileSql = "insert into sys_file(id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + this.fk_fileId + "\', \'" + e + "\',\'" + e + "\', " + var35 + ", \'" + var30 + "\', \'" + this.bodyHashCode + "\', \'" + UUID.randomUUID().toString().replace("-", "") + "\', \'" + Utils.parseData(now, "yyyy-MM-dd HH:mm:ss") + "\', \'" + isPic + "\', " + width + ", " + height + ")";
                this.st.addBatch(fileSql);
            } catch (Exception var25) {
                Logger.error(var25.getMessage());
            }
        }

        return this.body;
    }

    private void parseContent(Multipart multipart) throws MessagingException, IOException {
        try {
            int j = 0;

            for(int n = multipart.getCount(); j < n; ++j) {
                BodyPart part = multipart.getBodyPart(j);
                if(part.getContent() instanceof Multipart) {
                    this.parseContent((Multipart)part.getContent());
                } else {
                    String type = this.getType(part.getContentType());
                    if(type.startsWith("text/plain")) {
                        this.bodytext.append(part.getContent());
                    } else if(type.startsWith("text/html")) {
                        this.bodyHtml.append(part.getContent());
                    }

                    String disposition = part.getDisposition();
                    if(disposition != null) {
                        String filename = part.getFileName();
                        filename = this.getHeadStringValue(filename);
                        String hashId = DBUtils.uuid();
                        String ext = Utils.getExt(filename);
                        String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
                        String filepath = Utils.getFilePath(hashId);
                        File f = new File(basePath + filepath);
                        if(!f.exists()) {
                            f.mkdirs();
                        }

                        File file = new File(basePath + filepath + "/" + hashId + "." + ext);
                        BufferedOutputStream bw = null;
                        BufferedInputStream bin = null;

                        try {
                            bin = new BufferedInputStream(part.getInputStream());
                            bw = new BufferedOutputStream(new FileOutputStream(file));
                            byte[] hashCode = new byte[1024];
                            boolean isPic = false;

                            int var39;
                            while((var39 = bin.read(hashCode)) != -1) {
                                bw.write(hashCode, 0, var39);
                            }
                        } catch (Exception var35) {
                            var35.printStackTrace();
                        } finally {
                            if(bw != null) {
                                bw.close();
                            }

                            if(bin != null) {
                                bin.close();
                            }

                        }

                        String var38 = Utils.getSha1(file);
                        String var40 = "N";
                        int width = 0;
                        int height = 0;
                        boolean pic = Utils.isPicFile(file);
                        if(pic) {
                            var40 = "Y";
                            BufferedImage fileSize = ImageIO.read(file);
                            width = (int)((double)fileSize.getWidth((ImageObserver)null));
                            height = (int)((double)fileSize.getHeight((ImageObserver)null));
                        }

                        long var41 = file.length();
                        String name = Utils.getFileNameWithoutExt(filename);
                        EntityImpl en = new EntityImpl(this.conn);
                        en.executeQuery("select id  from sys_hash a where a.hash_code=\'" + var38 + "\'");
                        String fileName2;
                        if(en.getResultCount() <= 0) {
                            fileName2 = "insert into sys_hash(id,hash_code,filename,file_size,extension)VALUES(\'" + hashId + "\',\'" + var38 + "\',\'" + filename + "\'," + var41 + ",\'" + ext + "\')";
                            this.st.addBatch(fileName2);
                        } else {
                            hashId = en.getStringValue("id");
                            file.delete();
                        }

                        fileName2 = filename;
                        int size = en.executeQuery("select count(*) num from sys_file a where a.filename=\'" + filename + "\'");
                        if(size > 0 && en.getIntegerValue("num").intValue() > 0) {
                            fileName2 = name + "(" + (en.getIntegerValue("num").intValue() + 1) + ")." + ext;
                        }

                        Date now = new Date();
                        String fk_fileId = DBUtils.uuid();
                        String fileSql = "insert into sys_file(id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + fk_fileId + "\', \'" + filename + "\',\'" + fileName2 + "\', " + var41 + ", \'" + ext + "\', \'" + var38 + "\', \'" + UUID.randomUUID().toString().replace("-", "") + "\', \'" + Utils.parseData(now, "yyyy-MM-dd HH:mm:ss") + "\', \'" + var40 + "\', " + width + ", " + height + ")";
                        this.st.addBatch(fileSql);
                        String appxSql = "insert into SYS_APPENDIX(id,FK_MESSAGE_ID,FK_APPX_ID,HASH_CODE) VALUES (\'" + DBUtils.uuid() + "\',\'" + this.getUid() + "\',\'" + fk_fileId + "\',\'" + var38 + "\')";
                        this.st.addBatch(appxSql);
                        SFile fw = new SFile();
                        fw.setCreateTime(now);
                        fw.setExt(ext);
                        fw.setFileName(filename);
                        fw.setFileSize(var41);
                        fw.setFileStringSize(Utils.getFileStringSize(var41));
                        fw.setFileType(SFileType.FILE);
                        fw.setHashCode(var38);
                        fw.setHashId(hashId);
                        fw.setId(fk_fileId);
                        fw.setPic(false);
                        fw.setPid("-1");
                        fw.setRename(fileName2);
                        this.appx.add(fw);
                    }
                }
            }
        } catch (Exception var37) {
            ;
        }

    }

    private void getCharset() {
        if(this.charset == null) {
            try {
                Enumeration type = this.message.getMatchingHeaderLines(this.CHARTSET_HEADER);

                while(type.hasMoreElements()) {
                    String types = (String)type.nextElement();
                    Matcher length = encodeStringPattern.matcher(types);
                    if(length.find()) {
                        this.headCharset = length.group(1).toLowerCase();
                        this.headCharset = this.headCharset.split(" ")[0];
                        this.headCharset = this.headCharset.split("\\?")[0];
                        break;
                    }
                }
            } catch (MessagingException var9) {
                ;
            }

            try {
                String var10 = this.message.getContentType();
                if(var10 != null) {
                    String[] var11 = var10.split(";");
                    int var12 = var11.length;
                    if(var12 > 0) {
                        for(int i = 0; i < var12; ++i) {
                            String temp = var11[i];
                            if(temp != null) {
                                temp = temp.trim().toLowerCase();
                                if(temp.contains("charset")) {
                                    String[] ts = temp.split("=");
                                    if(ts.length == 2) {
                                        String t = ts[1];
                                        t = t.replace("\"", "");
                                        t = t.replace("\'", "");
                                        t = t.replace("\\r", "");
                                        t = t.replace("\\n", "");
                                        t = t.replace("\\t", "");
                                        this.bodyCharset = t.trim();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (MessagingException var8) {
                ;
            }

            if(this.headCharset == null) {
                if(this.bodyCharset != null) {
                    this.charset = this.bodyCharset;
                    this.headCharset = this.bodyCharset;
                } else {
                    this.charset = "";
                }
            } else if(this.headCharset != null) {
                if(this.bodyCharset == null) {
                    this.bodyCharset = this.headCharset;
                    this.charset = this.bodyCharset;
                } else {
                    this.charset = this.bodyCharset;
                }
            }
        }

    }

    private String getType(String type) {
        if(type == null) {
            type = "";
        }

        type = type.trim().toLowerCase();
        if(type.startsWith("text/plain")) {
            type = "text/plain";
        } else if(type.startsWith("text/html")) {
            type = "text/html";
        } else if(type.startsWith("image/")) {
            type = "image";
        } else if(type.startsWith("message/")) {
            type = "message";
        } else if(type.startsWith("application/")) {
            type = "application";
        } else if(type.startsWith("multipart/")) {
            type = "multipart";
        } else {
            Logger.warn("unknown type:" + type);
        }

        return type;
    }

    public String getMessageId() {
        if(this.messageId == null) {
            try {
                this.messageId = this.message.getMessageID();
            } catch (Exception var2) {
                this.messageId = "";
            }
        }

        return this.messageId;
    }

    public List<SFile> getAppx() {
        return this.appx;
    }

    public String getHeadCharset() {
        return this.headCharset;
    }

    public String getBodyCharset() {
        return this.bodyCharset;
    }

    public String getHashId() {
        return this.hashId;
    }

    public String getFk_fileId() {
        return this.fk_fileId;
    }
}
