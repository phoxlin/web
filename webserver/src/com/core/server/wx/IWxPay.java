package com.core.server.wx;

import java.sql.Connection;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public interface IWxPay {
    void createBusinessData(String var1, String var2, Map<String, Object> var3,
                            long var4, String var6, Connection var7) throws Exception;

    void updateBusinessData(String var1, Connection var2) throws Exception;
}
