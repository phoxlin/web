package com.core;

import com.core.server.c.CItem;

import java.sql.Connection;
import java.util.List;

public interface IUser {
    List<CItem> getUserListBySet(String var1, String var2, Connection conn) throws Exception;
}
