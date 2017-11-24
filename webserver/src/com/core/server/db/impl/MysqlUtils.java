package com.core.server.db.impl;

import com.core.SFile;
import com.core.server.db.DBUtils;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class MysqlUtils extends DBUtils {
    public SFile backup(String id, Connection conn, boolean auto) throws Exception {
        if(this.server == null) {
            this.initBakDB(conn);
        }

        String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
        String filepath = Utils.getFilePath(id);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        String ext = "bak";
        String name = this.server.getBackupFileName(auto);
        String filename = name + "." + ext;
        File file = new File(basePath + filepath + "/" + id + "." + ext);
        Process child = null;
        InputStream in = null;
        InputStreamReader xx = null;
        FileOutputStream fout = null;
        OutputStreamWriter writer = null;
        BufferedReader br = null;

        try {
            Runtime s = Runtime.getRuntime();
            child = s.exec(this.server.getBackupCmd());
            in = child.getInputStream();
            xx = new InputStreamReader(in, "utf8");
            String files = null;
            StringBuffer sb = new StringBuffer();
            br = new BufferedReader(xx);

            while(true) {
                if((files = br.readLine()) == null) {
                    fout = new FileOutputStream(file);
                    writer = new OutputStreamWriter(fout, "utf8");
                    writer.write(sb.toString());
                    writer.flush();
                    break;
                }

                sb.append(files + "\r\n");
            }
        } catch (Exception var23) {
            throw var23;
        } finally {
            if(child != null) {
                child = null;
            }

            if(in != null) {
                in.close();
            }

            if(xx != null) {
                xx.close();
            }

            if(br != null) {
                br.close();
            }

            if(fout != null) {
                fout.close();
            }

            if(writer != null) {
                writer.close();
            }

        }

        new SFile();
        SFile s1;
        if(this.server.isZip()) {
            ArrayList files1 = new ArrayList();
            files1.add(file);
            ext = "zip";
            filename = name + "." + ext;
            s1 = Utils.zip(id, files1, filename, "/", conn);
            file.delete();
        } else {
            s1 = Utils.saveFile(file, filename, "-1", "-1", DBUtils.uuid());
        }

        return s1;
    }

    public SFile getback(String id, Connection conn) throws Exception {
        if(this.server == null) {
            this.initBakDB(conn);
        }

        SFile s = null;
        Process child = null;
        OutputStream out = null;
        BufferedReader br = null;
        OutputStreamWriter writer = null;

        try {
            SFile e = Utils.getUploadFileInfo(id);
            s = e;
            if(e.getExt().equals("zip")) {
                List rt = Utils.unzip(new File(e.getPath()), conn);
                e = (SFile)rt.get(0);
            }

            Runtime rt1 = Runtime.getRuntime();
            child = rt1.exec(this.server.getGetupCmd());
            out = child.getOutputStream();
            String inStr = null;
            StringBuffer sb = new StringBuffer();
            sb.append("set collation_server=utf8_general_ci;");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(e.getPath()), "utf8"));

            while((inStr = br.readLine()) != null) {
                sb.append(inStr + "\r\n");
            }

            writer = new OutputStreamWriter(out, "utf8");
            writer.write(sb.toString());
            writer.flush();
            return s;
        } catch (Exception var15) {
            throw var15;
        } finally {
            if(child != null) {
                child = null;
            }

            if(out != null) {
                out.close();
            }

            if(br != null) {
                br.close();
            }

            if(writer != null) {
                writer.close();
            }

        }
    }
}
