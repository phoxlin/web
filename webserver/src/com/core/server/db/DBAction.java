package com.core.server.db;

import com.core.SFile;
import com.core.server.BasicAction;
import com.core.server.Route;

import java.net.URLDecoder;
import java.util.Date;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DBAction extends BasicAction {
    @Route(
            value = "/db-importPDMFile",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void importPDMFile() throws Exception {
        String upload_pdm = this.request.getParameter("upload_pdm");
        upload_pdm = URLDecoder.decode(upload_pdm, "UTF-8");
        String[] names = upload_pdm.split("_._");
        if(upload_pdm != null && names.length == 2) {
            String filename = names[1];
            SFile sf = Utils.downloadFile(upload_pdm, filename);
            new PowerDesignerParser(sf.getPath(), (String)null);
        } else {
            throw new Exception("上传的文件路径不对");
        }
    }

    @Route(
            value = "/db-backup_now",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void dbBackupNow() throws Exception {
        MysqlUtils utils = new MysqlUtils();
        utils.initBakDB(this.getConnection());
        SFile s = utils.backup(DBUtils.uuid(), this.getConnection(), false);
        EntityImpl sys_db_record = new EntityImpl("sys_db_record", this.getConnection());
        sys_db_record.setValue("OP_TYPE", "001");
        sys_db_record.setValue("OP_TIME", new Date());
        sys_db_record.setValue("FILE_NAME", s.getFileName());
        sys_db_record.setValue("IS_ZIP", utils.server.isZip()?"Y":"N");
        sys_db_record.setValue("BAK_SIZE", Long.valueOf(s.getFileSize()));
        sys_db_record.setValue("HASH_CODE", s.getHashCode());
        sys_db_record.setValue("RESULT", "001");
        sys_db_record.setValue("fk_file_id", s.getId());
        sys_db_record.create();
    }

    @Route(
            value = "/db-getback-byfile",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void saveDBFile() throws Exception {
        String id = this.request.getParameter("upload_db_file");
        MysqlUtils utils = new MysqlUtils();
        EntityImpl sys_db_record = new EntityImpl("sys_db_record", this);
        sys_db_record.setValue("OP_TYPE", "002");
        SFile s = utils.getback(id, this.getConnection());
        sys_db_record.setValue("OP_TIME", new Date());
        sys_db_record.setValue("FILE_NAME", s.getFileName());
        sys_db_record.setValue("IS_ZIP", "zip".equals(s.getExt())?"Y":"N");
        sys_db_record.setValue("BAK_SIZE", Long.valueOf(s.getFileSize()));
        sys_db_record.setValue("HASH_CODE", s.getHashCode());
        sys_db_record.setValue("fk_file_id", s.getId());
        sys_db_record.setValue("RESULT", "001");
        sys_db_record.create();
    }

    @Route(
            value = "/db-getback",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void getback() throws Exception {
        String id = this.request.getParameter("id");
        MysqlUtils utils = new MysqlUtils();
        EntityImpl sys_db_record = new EntityImpl("sys_db_record", this);
        sys_db_record.setValue("OP_TYPE", "002");
        SFile s = utils.getback(id, this.getConnection());
        sys_db_record.setValue("OP_TIME", new Date());
        sys_db_record.setValue("FILE_NAME", s.getFileName());
        sys_db_record.setValue("IS_ZIP", utils.server.isZip()?"Y":"N");
        sys_db_record.setValue("BAK_SIZE", Long.valueOf(s.getFileSize()));
        sys_db_record.setValue("HASH_CODE", s.getHashCode());
        sys_db_record.setValue("fk_file_id", s.getId());
        sys_db_record.setValue("RESULT", "001");
        sys_db_record.create();
    }
}
