package com.core.server.db;

import com.core.SFile;
import com.core.Task;
import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.db.impl.MysqlUtils;
import com.core.server.log.Logger;
import com.core.server.tools.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DBTask extends Task {
    private DBUtils utils = new MysqlUtils();
    private static boolean inited = false;

    public DBTask() throws Exception {
        this.name = "数据库备份";
        if(!inited) {
            inited = true;
            (new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            DBTask.this.check();
                            Thread.sleep(300000L);
                        } catch (Exception var2) {
                            ;
                        }
                    }
                }
            })).start();
        }

    }

    public void check() throws Exception {
        DBM db = new DBM();
        Connection conn = null;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            this.utils.initBakDB(conn);
            this.setRegulation(this.utils.server.getRule());
            String e = this.utils.server.getSaveType();
            int saveVal = this.utils.server.getSaveVal();
            HashMap hashs = new HashMap();
            EntityImpl en = new EntityImpl(conn);
            String ext;
            String filepath;
            String path;
            if(saveVal > 0) {
                Statement e1 = conn.createStatement();
                int size;
                if("002".equals(e)) {
                    size = en.executeQuery("select a.id,a.fk_file_id,a.hash_code,b.id hashid,b.extension from sys_db_record a ,sys_hash b where a.hash_code=b.hash_code and a.op_type=\'001\' order by a.op_time ");
                    if(size > saveVal) {
                        for(int hashid = 0; hashid < size - saveVal; ++hashid) {
                            ext = en.getStringValue("id", hashid);
                            filepath = en.getStringValue("fk_file_id", hashid);
                            path = en.getStringValue("hash_code", hashid);
                            hashs.put(en.getStringValue("hashid", hashid), en.getStringValue("extension", hashid));
                            SFile f = new SFile(filepath);
                            f.deleteFile();
                            e1.addBatch("delete from sys_file where id = \'" + filepath + "\'");
                            e1.addBatch("delete from sys_hash where hash_code = \'" + path + "\'");
                            e1.addBatch("delete from sys_db_record where id =\'" + ext + "\'");
                        }
                    }
                } else {
                    String fk_file_id;
                    if("003".equals(e)) {
                        long var32 = System.currentTimeMillis();
                        long var35 = 86400000L;
                        long var38 = var32 - var35 * (long)saveVal;
                        Date id = new Date(var38);
                        fk_file_id = DBUtils.formartDate(id, "yyyy-MM-dd");
                        int file = en.executeQuery("select a.id,a.fk_file_id,a.hash_code,a.op_time,b.id hashid,b.extension from sys_db_record a,sys_hash b where a.hash_code=b.hash_code and a.op_type=\'001\' and a.op_time <\'" + fk_file_id + "\'");

                        for(int hash_code = 0; hash_code < file; ++hash_code) {
                            String bak_size = en.getStringValue("id", hash_code);
                            String fk_file_id1 = en.getStringValue("fk_file_id", hash_code);
                            String hash_code1 = en.getStringValue("hash_code", hash_code);
                            SFile file1 = new SFile(fk_file_id1);
                            file1.deleteFile();
                            hashs.put(en.getStringValue("hashid", hash_code), en.getStringValue("extension", hash_code));
                            e1.addBatch("delete from sys_file where id = \'" + fk_file_id1 + "\'");
                            e1.addBatch("delete from sys_hash where hash_code = \'" + hash_code1 + "\'");
                            e1.addBatch("delete from sys_db_record where id =\'" + bak_size + "\'");
                        }
                    } else if("004".equals(e)) {
                        size = en.executeQuery("select a.id,a.fk_file_id,a.hash_code,a.bak_size,b.id hashid,b.extension from sys_db_record a,sys_hash b where a.hash_code=b.hash_code and a.op_type=\'001\' order by a.op_time desc");
                        if(size > 0) {
                            long var34 = 0L;
                            long var37 = (long)(saveVal * 1024 * 1024);

                            for(int var39 = 0; var39 < size; ++var39) {
                                String var41 = en.getStringValue("id", var39);
                                fk_file_id = en.getStringValue("fk_file_id", var39);
                                SFile var42 = new SFile(fk_file_id);
                                var42.deleteFile();
                                String var43 = en.getStringValue("hash_code", var39);
                                long var44 = en.getLongValue("bak_size", var39).longValue();
                                var34 += var44;
                                if(var34 > var37) {
                                    hashs.put(en.getStringValue("hashid", var39), en.getStringValue("extension", var39));
                                    e1.addBatch("delete from sys_file where id = \'" + fk_file_id + "\'");
                                    e1.addBatch("delete from sys_hash where hash_code = \'" + var43 + "\'");
                                    e1.addBatch("delete from sys_db_record where id =\'" + var41 + "\'");
                                }
                            }
                        }
                    }
                }

                try {
                    e1.executeBatch();
                } catch (Exception var28) {
                    ;
                }
            }

            conn.commit();
            if(hashs.size() > 0) {
                Iterator var33 = hashs.entrySet().iterator();

                while(var33.hasNext()) {
                    Map.Entry var31 = (Map.Entry)var33.next();
                    String var36 = (String)var31.getKey();
                    ext = (String)var31.getValue();
                    filepath = Utils.getFilePath(var36);
                    path = SFile.basePath + filepath + "/" + var36 + "." + ext;
                    File var40 = new File(path);

                    try {
                        var40.delete();
                        Logger.info("deleted db bak file:" + path);
                    } catch (Exception var27) {
                        ;
                    }
                }
            }
        } catch (Exception var29) {
            conn.rollback();
            Logger.error(Utils.getErrorStack(var29));
        } finally {
            db.freeConnection(conn);
        }

    }

    public String execute(String executeId, Connection conn) throws Exception {
        SFile s = this.utils.backup(DBUtils.uuid(), conn, true);
        EntityImpl sys_db_record = new EntityImpl("sys_db_record", conn);
        sys_db_record.setValue("OP_TYPE", "001");
        sys_db_record.setValue("OP_TIME", s.getCreateTime());
        sys_db_record.setValue("FILE_NAME", s.getFileName());
        sys_db_record.setValue("IS_ZIP", this.utils.server.isZip()?"Y":"N");
        sys_db_record.setValue("BAK_SIZE", Long.valueOf(s.getFileSize()));
        sys_db_record.setValue("HASH_CODE", s.getHashCode());
        sys_db_record.setValue("RESULT", "001");
        sys_db_record.setValue("fk_file_id", s.getId());
        sys_db_record.create();
        return "";
    }
}
