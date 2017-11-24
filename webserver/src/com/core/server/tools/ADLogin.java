package com.core.server.tools;

import com.core.server.log.Logger;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class ADLogin {
    private static String host = Resources.getProperty("ad_host", "semmchina.com");
    private static int port = Resources.getIntProperty("ad_port", 389);


    public static void ldapLogin(String userName, String password) throws Exception {
        String url = new String("ldap://" + host + ":" + port);
        Hashtable env = new Hashtable();
        InitialDirContext ctx = null;
        env.put("java.naming.security.authentication", "simple");
        env.put("java.naming.security.principal", "semmchina\\" + userName);
        env.put("java.naming.security.credentials", password);
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", url);

        try {
            ctx = new InitialDirContext(env);
        } catch (Exception e) {
            Logger.error(e);
            throw new Exception("用户(" + userName + ")AD验证失败");
        } finally {
            if(ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    ;
                }
            }

        }

    }
}
