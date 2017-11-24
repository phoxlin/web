package com.core;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/24.
 */
public class SFile {
    private String id;
    private String hashId;
    private String hashCode;
    private String fileName;
    private String fileNameWithoutExt;
    private long fileSize;
    private String fileStringSize;
    private String ext;
    private String path;
    private String rename;
    private boolean isPic;
    private SFileType fileType;
    private String pid;
    private Date createTime;
    private int width;
    private int height;
    private String userId;
    private File file;
    public static String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
    private String remark;

    public SFile() {
    }

    public static SFile createFileByJson(String str) throws Exception {
        SFile sf = new SFile();

        try {
            JSONObject e = new JSONObject(str);
            sf.setId(e.getString("id"));
            sf.setHashId("");
            sf.setHashCode(e.getString("hc"));
            sf.setFileName(e.getString("ne"));
            sf.setRename(e.getString("rn"));
            sf.setFileSize(e.getLong("fs"));
            sf.setPic("Y".equals(e.getString("pic")));
            sf.setFileType("FILE".equalsIgnoreCase(e.getString("t"))?SFileType.FILE:SFileType.FOLDER);
            sf.setWidth(e.getInt("w"));
            sf.setHeight(e.getInt("h"));
            return sf;
        } catch (Exception var3) {
            throw new Exception("通过Json字符串，反序列化失败：" + var3.getMessage());
        }
    }

    public String toString() {
        JSONObject o = new JSONObject();
        o.put("id", this.id);
        o.put("hId", this.hashId);
        o.put("hc", this.hashCode);
        o.put("ne", this.fileName);
        o.put("rn", this.rename);
        o.put("fs", this.fileSize);
        o.put("pic", this.isPic?"Y":"N");
        o.put("t", this.fileType);
        o.put("w", this.width);
        o.put("h", this.height);
        o.put("uId", this.userId);
        return o.toString();
    }

    public SFile(String fileId) throws Exception {
        SFile file = createSFileById(fileId);
        this.id = file.getId();
        this.hashId = file.getHashId();
        this.hashCode = file.getHashCode();
        this.fileName = file.getFileName();
        this.fileNameWithoutExt = file.getFileNameWithoutExt();
        this.fileSize = file.getFileSize();
        this.fileStringSize = file.getFileStringSize();
        this.ext = file.getExt();
        this.path = file.getPath();
        this.rename = file.getRename();
        this.isPic = file.isPic;
        this.fileType = file.getFileType();
        this.pid = file.getPid();
        this.createTime = file.getCreateTime();
        this.width = file.getWidth();
        this.height = file.getHeight();
        this.userId = file.getUserId();
        this.file = file.getFile();
    }

    public static SFile createSFileById(String fileId) throws Exception {
        SFile file = null;
        Jedis jd = null;

        SFile var9;
        try {
            jd = RedisUtils.getConnection();
            boolean hasHashCode = jd.hexists(SystemUtils.PROJECT_NAME, "fileId_" + fileId).booleanValue();
            if(hasHashCode) {
                String db = jd.hget(SystemUtils.PROJECT_NAME, "fileId_" + fileId);
                String conn = jd.hget(SystemUtils.PROJECT_NAME, db);
                file = createFileByJson(conn);
            }

            if(file != null) {
                SFile var11 = file;
                return var11;
            }

            file = new SFile();
            DBM db1 = new DBM();
            Connection conn1 = null;

            try {
                jd = RedisUtils.getConnection();
                conn1 = db1.getConnection();
                conn1.setAutoCommit(false);
                EntityImpl e = new EntityImpl(conn1);
                int size = e.executeQuery("select a.id,a.extension,a.filename,a.file_size,a.hash_code,a.height,a.width,a.is_pic,a.pid,a.re_name from sys_file a where a.id=?", new String[]{fileId});
                if(size <= 0) {
                    throw new Exception("系统找不到相关的文件【" + fileId + "】");
                }

                file.setId(e.getStringValue("id"));
                file.setExt(e.getStringValue("extension"));
                file.setFileName(e.getStringValue("filename"));
                file.setFileSize(e.getLongValue("file_size").longValue());
                file.setFileType(SFileType.FILE);
                file.setHashCode(e.getStringValue("hash_code"));
                file.setHeight(e.getIntegerValue("height").intValue());
                file.setWidth(e.getIntegerValue("width").intValue());
                file.setPic(e.getStringValue("is_pic").equalsIgnoreCase("Y"));
                file.setPid(e.getStringValue("pid"));
                file.setRename(e.getStringValue("re_name"));
                jd.hset(SystemUtils.PROJECT_NAME, file.getHashCode(), file.toString());
                jd.hset(SystemUtils.PROJECT_NAME, "fileId_" + file.getId(), file.getHashCode());
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##fileId_" + file.getId(), "s");
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##" + file.getHashCode(), "s");
                conn1.commit();
                var9 = file;
            } catch (Exception var18) {
                throw new Exception("系统找不到相关的文件【" + fileId + "】");
            } finally {
                db1.freeConnection(conn1);
            }
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var9;
    }

    public static SFile createSFileByHashCode(String hashcode) throws Exception {
        SFile file = null;
        Jedis jd = null;

        SFile var9;
        try {
            jd = RedisUtils.getConnection();
            boolean hasHashCode = jd.hexists(SystemUtils.PROJECT_NAME, hashcode).booleanValue();
            if(hasHashCode) {
                String db = jd.hget(SystemUtils.PROJECT_NAME, hashcode);
                file = createFileByJson(db);
            }

            if(file != null) {
                SFile var11 = file;
                return var11;
            }

            file = new SFile();
            DBM db1 = new DBM();
            Connection conn = null;

            try {
                jd = RedisUtils.getConnection();
                conn = db1.getConnection();
                conn.setAutoCommit(false);
                EntityImpl e = new EntityImpl(conn);
                int size = e.executeQuery("select a.id,a.extension,a.filename,a.file_size,a.hash_code,a.height,a.width,a.is_pic,a.pid,a.re_name from sys_file a where a.hash_code=?", new String[]{hashcode});
                if(size <= 0) {
                    throw new Exception("系统找不到相关的文件HashCode【" + hashcode + "】");
                }

                file.setId(e.getStringValue("id"));
                file.setExt(e.getStringValue("extension"));
                file.setFileName(e.getStringValue("filename"));
                file.setFileSize(e.getLongValue("file_size").longValue());
                file.setFileType(SFileType.FILE);
                file.setHashCode(e.getStringValue("hash_code"));
                file.setHeight(e.getIntegerValue("height").intValue());
                file.setWidth(e.getIntegerValue("width").intValue());
                file.setPic(e.getStringValue("is_pic").equalsIgnoreCase("Y"));
                file.setPid(e.getStringValue("pid"));
                file.setRename(e.getStringValue("re_name"));
                jd.hset(SystemUtils.PROJECT_NAME, file.getHashCode(), file.toString());
                jd.hset(SystemUtils.PROJECT_NAME, "fileId_" + file.getId(), file.getHashCode());
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##fileId_" + file.getId(), "s");
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##" + file.getHashCode(), "s");
                conn.commit();
                var9 = file;
            } catch (Exception var18) {
                throw new Exception("系统找不到相关的文件HashCode【" + hashcode + "】");
            } finally {
                db1.freeConnection(conn);
            }
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var9;
    }

    public boolean deleteFile() {
        boolean ok = this.file.delete();
        if(ok) {
            Logger.info("Deleted FileStore file:" + this.getPath());
        } else {
            Logger.error("Deleted Failed, FileStore file:" + this.getPath());
        }

        return ok;
    }

    public String getRePath(String prefix) {
        String filepath = Utils.getFilePath(this.hashCode);
        return prefix + filepath + "/" + this.hashCode + "." + this.ext;
    }

    public boolean exists() {
        File file = this.getFile();
        return file == null?false:file.exists();
    }

    public String getUserId() {
        if(this.userId != null && this.userId.length() <= 0) {
            this.userId = null;
        }

        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public File getFile() {
        if(this.file == null) {
            this.file = new File(this.getPath());
        }

        return this.file;
    }

    public InputStream getInputStream() throws Exception {
        return new FileInputStream(new File(this.getPath()));
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileNameWithoutExt() {
        if(this.fileNameWithoutExt == null) {
            this.fileNameWithoutExt = Utils.getFileNameWithoutExt(this.getFileName());
        }

        return this.fileNameWithoutExt;
    }

    public String getHashId() {
        return this.hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public String getHashCode() {
        return this.hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileStringSize() {
        if(this.fileStringSize == null) {
            this.fileStringSize = Utils.getFileStringSize(this.fileSize);
        }

        return this.fileStringSize;
    }

    public void setFileStringSize(String fileStringSize) {
        this.fileStringSize = fileStringSize;
    }

    public String getExt() {
        if(this.ext == null) {
            this.ext = Utils.getExt(this.fileName);
        }

        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getPath() {
        String filepath = Utils.getFilePath(this.hashCode);
        this.path = basePath + filepath + "/" + this.hashCode + "." + this.getExt();
        return this.path;
    }

    public String getRename() {
        return this.rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public boolean isPic() {
        return this.isPic;
    }

    public void setPic(boolean isPic) {
        this.isPic = isPic;
    }

    public SFileType getFileType() {
        return this.fileType;
    }

    public void setFileType(SFileType fileType) {
        this.fileType = fileType;
    }

    public String getPid() {
        if(this.pid == null || this.pid.length() <= 0) {
            this.pid = "-1";
        }

        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
