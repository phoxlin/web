package com.core.server.db;

import java.sql.Connection;

/**
 * Created by chen_lin on 2017/11/24.
 */
public interface IDB {
    Connection getConnection() throws Exception;

    Connection getSlaveConnection() throws Exception;

    void freeConnection(Connection var1);
}
