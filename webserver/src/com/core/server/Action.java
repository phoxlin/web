package com.core.server;

import com.core.User;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

public abstract class Action {
    public JhLog L;
    public PrintWriter out;
    private Connection conn;
    private Map<String, String[]> parameters;
    private Set<String> uniqueCheck = new HashSet();
    public JSONObject obj = null;
    public static final String METHOD = "_REQUEST_METHOD_";
    public HttpServletRequest request;
    public HttpServletResponse response;

    public Action() {
    }

    public String getAccessToken() {
        String at = this.getParameter("jh_access_token");
        if(at == null || at.length() <= 0) {
            at = this.getCookie("jh_access_token");
        }

        return at;
    }

    public abstract User getSessionUser() throws Exception;

    public String getCookie(String key) {
        Cookie[] cookies = this.request.getCookies();
        if(cookies != null) {
            int i = 0;

            for(int l = cookies.length; i < l; ++i) {
                Cookie c = cookies[i];
                if(key.equalsIgnoreCase(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return "";
    }

    public String getReferer() {
        return this.request.getHeader("Referer");
    }

    public String getOrigin() {
        return this.request.getHeader("Origin");
    }

    public void setSessionAttr(String key, Object val) {
    }

    public void setRequestAttr(String key, Object val) {
        this.request.setAttribute(key, val);
    }

    public Object getSessionAttr(String key) {
        return null;
    }

    public String getStringSessionAttr(String key) {
        Object val = this.getSessionAttr(key);
        return val != null?val.toString():"";
    }

    public Object getRequestAttr(String key) {
        return null;
    }

    public String getStringRequestAttr(String key) {
        Object val = this.getRequestAttr(key);
        return val != null?val.toString():"";
    }

    public Connection getConnection() throws Exception {
        return this.conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    public Map<String, String[]> getParameters() {
        if(this.parameters == null) {
            this.parameters = new HashMap();
        }

        return this.parameters;
    }

    public Set<String> getParameterNames() {
        return this.getParameters().keySet();
    }

    public String getParameter(String name, int index) {
        String[] vs = (String[])this.getParameters().get(name);
        return vs != null && vs.length > index?vs[index]:"";
    }

    public String[] getParameterValues(String name) {
        return (String[])this.getParameters().get(name);
    }

    public String getParameter(String name) {
        String[] vs = (String[])this.getParameters().get(name);
        return vs != null && vs.length > 0?vs[0]:"";
    }

    public Entity getEntityFromPage(String name) throws Exception {
        return this.getEntityFromPage(name, 0);
    }

    public Entity getEntityFromPage(String name, int index) throws Exception {
        EntityImpl entity = new EntityImpl(name, this.getConnection());
        boolean hasValue = false;
        Set names = this.getParameterNames();
        Iterator it = names.iterator();

        while(it.hasNext()) {
            String key = (String)it.next();
            String[] keys = key.split("__");
            if(keys.length == 2 && keys[0] != null && keys[0].equalsIgnoreCase(name)) {
                String fieldname = keys[1];
                if(fieldname != null) {
                    String[] values = this.getParameterValues(key);
                    if(values != null && values.length > index) {
                        try {
                            entity.setValue(fieldname, values[index]);
                            entity.setValue(fieldname, values[index], 0);
                            if(index == 0) {
                                for(int e = 0; e < values.length; ++e) {
                                    entity.setValue(fieldname, values[e], e);
                                }
                            }

                            if(fieldname.endsWith("_2")) {
                                this.uniqueCheck.add(key);
                            }

                            Logger.debug("Got Entity[" + name.toLowerCase() + "] with index:[" + index + "] from page,[" + name.toLowerCase() + "__" + fieldname.toLowerCase() + "]:" + values[index]);
                            hasValue = true;
                        } catch (Exception var12) {
                            if(fieldname.endsWith("_2")) {
                                this.uniqueCheck.add(key);
                            }

                            Logger.warn("Get Entity[" + name.toLowerCase() + "] with index:[" + index + "] from page failed:" + var12);
                        }
                    } else {
                        Logger.warn("Can\'t get Entity[" + name.toLowerCase() + "] with field name:[" + fieldname + "],index:[" + index + "] from Page.");
                    }
                }
            }
        }

        if(hasValue) {
            return entity;
        } else {
            return null;
        }
    }

    public Set<String> getUniqueCheck() {
        return this.uniqueCheck;
    }
}
