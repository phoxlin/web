package com.core.server.db;

import java.util.Date;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DBServer {
    private String host;
    private int port;
    private String user_name;
    private String pwd;
    private String db_name;
    private String rule;
    private String backupCmd;
    private String getupCmd;
    private boolean zip;
    private String backupFileName;
    private String saveType;
    private int saveVal;

    public DBServer() {
    }

    public String getBackupFileName(boolean auto) {
        this.backupFileName = this.getDb_name() + "(" + DBUtils.formartDate(new Date(), "yyyy-MM-dd HH:mm:ss") + ") - (" + (auto?"auto":"manual") + ")";
        return this.backupFileName;
    }

    public String getBackupCmd() {
        if(this.backupCmd == null) {
            this.backupCmd = "mysqldump -u " + this.getUser_name() + " --password=" + this.getPwd() + " --default-character-set=utf8 -h " + this.getHost() + " -P " + this.getPort() + " " + this.getDb_name();
        }

        return this.backupCmd;
    }

    public String getGetupCmd() {
        if(this.getupCmd == null) {
            this.getupCmd = "mysql -u " + this.getUser_name() + " -h " + this.getHost() + " -P " + this.getPort() + " --password=" + this.getPwd() + " " + this.getDb_name();
        }

        return this.getupCmd;
    }

    public boolean isZip() {
        return this.zip;
    }

    public void setZip(boolean zip) {
        this.zip = zip;
    }

    public String getHost() {
        if(this.host == null) {
            this.host = Resources.getProperty("DATABASE_HOST", "localhost");
        }

        return this.host;
    }

    public int getPort() {
        if(this.port <= 0) {
            this.port = Resources.getIntProperty("DATABASE_PORT", 3306);
        }

        return this.port;
    }

    public String getUser_name() {
        if(this.user_name == null) {
            this.user_name = Resources.getProperty("LOGIN_ID");
        }

        return this.user_name;
    }

    public String getPwd() {
        if(this.pwd == null) {
            this.pwd = Resources.getProperty("LOGIN_PASSWORD");
        }

        return this.pwd;
    }

    public String getDb_name() {
        if(this.db_name == null) {
            this.db_name = Resources.getProperty("DATABASE_NAME", "frame");
            this.db_name = this.db_name.split("\\?")[0];
        }

        return this.db_name;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSaveType() {
        return this.saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public int getSaveVal() {
        return this.saveVal;
    }

    public void setSaveVal(int saveVal) {
        this.saveVal = saveVal;
    }
}
