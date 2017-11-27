package com.core.server.wxtools;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class MySSLSocketFactory extends SSLSocketFactory {
    private static MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory(createSContext());

    static {
        mySSLSocketFactory = null;
    }

    private static SSLContext createSContext() {
        SSLContext sslcontext = null;

        try {
            sslcontext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        try {
            sslcontext.init((KeyManager[])null, new TrustManager[]{new TrustAnyTrustManager()}, (SecureRandom)null);
            return sslcontext;
        } catch (KeyManagementException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private MySSLSocketFactory(SSLContext sslContext) {
        super(sslContext);
        this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    public static MySSLSocketFactory getInstance() {
        return mySSLSocketFactory != null?mySSLSocketFactory:(mySSLSocketFactory = new MySSLSocketFactory(createSContext()));
    }
}
