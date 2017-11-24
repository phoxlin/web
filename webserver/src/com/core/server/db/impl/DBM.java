package com.core.server.db.impl;

import com.core.server.db.IDB;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DBM implements IDB {
    private DBType type;
    private int minConnectionNum;
    private int maxConnectionNum;
    private String dbDriver;
    private String dbServer;
    private ComboPooledDataSource masterDataSource = null;

    public DBM() {
    }

    public DBM(DBType type, String host, int port, String database, String loginName, String pwd, int minConnectionNum, int maxConnectionNum) throws Exception {
        this.type = type;
        this.minConnectionNum = minConnectionNum;
        this.maxConnectionNum = maxConnectionNum;
        if(this.type == DBType.Mysql) {
            this.dbDriver = "com.mysql.jdbc.Driver";
            this.dbServer = "jdbc:mysql://" + host + ":" + port + "/" + database;
        } else if(this.type == DBType.Oracle) {
            this.dbDriver = "oracle.jdbc.driver.OracleDriver";
            this.dbServer = "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
        } else {
            if(this.type == DBType.DB2) {
                throw new Exception("暂时没有实现DB2数据库连接");
            }

            if(this.type == DBType.Sqlserver) {
                throw new Exception("暂时没有实现Sqlserver数据库连接");
            }
        }

        this.masterDataSource = new ComboPooledDataSource();
        this.masterDataSource.setDriverClass(this.dbDriver);
        this.masterDataSource.setMaxPoolSize(this.maxConnectionNum);
        this.masterDataSource.setMinPoolSize(this.minConnectionNum);
        this.masterDataSource.setInitialPoolSize(maxConnectionNum / 3);
        this.masterDataSource.setMaxIdleTime(60);
        this.masterDataSource.setAutomaticTestTable("sys_testdb");
        this.masterDataSource.setTestConnectionOnCheckin(true);
        this.masterDataSource.setTestConnectionOnCheckout(true);
        this.masterDataSource.setIdleConnectionTestPeriod(18000);
        this.masterDataSource.setAutoCommitOnClose(true);
        this.masterDataSource.setAcquireIncrement(3);
        this.masterDataSource.setCheckoutTimeout(0);
        this.masterDataSource.setAcquireRetryAttempts(30);
        this.masterDataSource.setMaxStatements(0);
        this.masterDataSource.setMaxStatementsPerConnection(0);
        this.masterDataSource.setJdbcUrl(this.dbServer);
        this.masterDataSource.setUser(loginName);
        this.masterDataSource.setPassword(pwd);
    }

    public Connection getConnection() throws Exception {
        if(this.masterDataSource != null) {
            Connection conn = null;

            try {
                conn = this.masterDataSource.getConnection();
                conn.setTransactionIsolation(2);
                conn.setAutoCommit(false);
                return conn;
            } catch (SQLException var3) {
                throw new Exception("Can\'t get DB connection:" + var3);
            }
        } else {
            return DBUtils.getConnection();
        }
    }

    public void freeConnection(Connection conn) {
        DBUtils.freeConnection(conn);
    }

    public Connection getSlaveConnection() throws Exception {
        return DBUtils.getSlaveConnection();
    }
}
