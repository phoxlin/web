package com.core.server.tools;

import com.core.User;
import com.core.server.db.DBUtils;
import com.core.server.log.Logger;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class SystemUtils {
    public static String PROJECT_NAME = Resources.getProperty("project_name", "default");
    public static byte[] PROJECT_NAME_BYTE;
    public static int sessionTime;

    static {
        PROJECT_NAME_BYTE = PROJECT_NAME.getBytes();
        sessionTime = Resources.getIntProperty("sessiontime", 86400);
    }

    public SystemUtils() {
    }

    public static void invalidate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sid = getSessionId(request, response);
        Jedis jedis = null;

        try {
            jedis = RedisUtils.getConnection();
            jedis.expire(sid.getBytes(), 0);
            jedis.expire(sid, 0);
        } catch (Exception var8) {
            ;
        } finally {
            RedisUtils.freeConnection(jedis);
        }

    }

    public static void setApplicationAttr(String section, String key, Object val) throws Exception {
        setApplicationAttr(section + "##" + key, val);
    }

    public static Object getApplicationAttr(String section, String key) throws Exception {
        return getApplicationAttr(section + "##" + key);
    }

    private static void setApplicationAttr(String key, Object val) throws Exception {
        RedisUtils.setParam(PROJECT_NAME + "__" + key, val);
    }

    private static Object getApplicationAttr(String key) throws Exception {
        return RedisUtils.getParam(PROJECT_NAME + "__" + key);
    }

    public static void setSessionAttr(String section, String key, Object val, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionAttr(section + "##" + key, val, request, response);
    }

    private static void setSessionAttr(String key, Object val, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sid = getSessionId(request, response);
        String tKey = PROJECT_NAME + "__" + sid + "__" + key;
        Jedis jedis = null;

        try {
            jedis = RedisUtils.getConnection();
            RedisUtils.setHParam(sid, tKey, val, jedis, sessionTime);
        } catch (Exception var11) {
            throw var11;
        } finally {
            RedisUtils.freeConnection(jedis);
        }

    }

    public static Object getSessionAttr(String section, String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getSessionAttr(section + "##" + key, request, response);
    }

    private static Object getSessionAttr(String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sid = getSessionId(request, response);
        String tKey = PROJECT_NAME + "__" + sid + "__" + key;
        Jedis jedis = null;

        Object var8;
        try {
            jedis = RedisUtils.getConnection();
            var8 = RedisUtils.getHParam(sid, tKey, jedis, sessionTime);
        } catch (Exception var11) {
            throw var11;
        } finally {
            RedisUtils.freeConnection(jedis);
        }

        return var8;
    }

    public static User getSessionUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = null;

        try {
            user = (User)getSessionAttr("SESSIONUSER", User.NAME, request, response);
            if(user == null && !Resources.DEVELOPMENT) {
                Logger.warn("Can\'t get session user..");
            }
        } catch (Exception var4) {
            Logger.warn("Can\'t get session user..");
        }

        return user;
    }

    public static void setSessionUser(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setSessionAttr("SESSIONUSER", User.NAME, user, request, response);
    }

    public static String getSessionId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object sessionObject = request.getSession().getAttribute("jh_access_token");
        String sessionId = request.getParameter("jh_access_token");
        if(sessionId == null || sessionId.length() <= 0) {
            sessionId = request.getParameter("jh_access_token");
        }

        if((sessionId == null || sessionId.length() <= 0) && sessionObject != null) {
            sessionId = String.valueOf(sessionObject);
        }

        boolean fond = false;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            int c = 0;

            for(int l = cookies.length; c < l; ++c) {
                Cookie c1 = cookies[c];
                if((PROJECT_NAME + "__jh_access_token").equalsIgnoreCase(c1.getName())) {
                    fond = true;
                    if(sessionId != null && sessionId.length() > 0) {
                        c1.setValue(sessionId);
                        response.addCookie(c1);
                    } else {
                        sessionId = c1.getValue();
                    }
                    break;
                }
            }
        }

        if(sessionId == null || sessionId.length() <= 0) {
            sessionId = DBUtils.uuid();
        }

        if(!fond) {
            Cookie var9 = new Cookie(PROJECT_NAME + "__jh_access_token", sessionId);
            response.addCookie(var9);
        }

        request.getSession().setAttribute("jh_access_token", sessionId);
        return sessionId;
    }
}
