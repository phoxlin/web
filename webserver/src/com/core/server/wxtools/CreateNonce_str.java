package com.core.server.wxtools;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class CreateNonce_str {
    public static String createNonce_str() {
        String currTime = TenpayUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = String.valueOf(TenpayUtil.buildRandom(4));
        String strReq = strTime + strRandom;
        return strReq;
    }
}
