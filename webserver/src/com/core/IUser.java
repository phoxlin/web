package com.core;

import java.sql.Connection;
import java.util.List;

public interface IUser {
    List<CItem> getUserListBySet(String var1, String var2, Connection var3) throws Exception;
}
