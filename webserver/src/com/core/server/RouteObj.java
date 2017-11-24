package com.core.server;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouteObj {
    private boolean found = true;
    private String url;
    private String aliase;
    private HttpMethod[] m;
    private boolean regx;
    private String regUrl;
    private boolean conn;
    private boolean slave;
    private Class<Action> action;
    private String method;
    private ContentType type;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private boolean slience = false;
    private boolean realSlience = false;
    private int paramNum;

    public RouteObj() {
    }

    public String toString() {
        JSONObject o = new JSONObject();
        o.put("url", this.url);
        o.put("aliase", this.aliase);
        o.put("regUrl", this.regUrl);
        o.put("action:", this.action.getName());
        o.put("method", this.method);
        o.put("type", Utils.getListString(this.m));
        o.put("conn", this.conn);
        o.put("slave", this.slave);
        o.put("contentType", this.type);
        return o.toString();
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegUrl() {
        return this.regUrl;
    }

    public HttpMethod[] getM() {
        return this.m;
    }

    public void setM(HttpMethod[] m) {
        this.m = m;
    }

    public boolean isRegx() {
        return this.regx;
    }

    public Class<Action> getAction() {
        return this.action;
    }

    public void setAction(Class<Action> action) {
        this.action = action;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getParamNum() {
        return Integer.valueOf(this.paramNum);
    }

    public void setParamNum(Integer paramNum) {
        this.paramNum = paramNum.intValue();
    }

    public boolean isConn() {
        return this.conn;
    }

    public void setConn(boolean conn) {
        this.conn = conn;
    }

    public String getAliase() {
        return this.aliase;
    }

    public void setAliase(String aliase) {
        this.aliase = aliase;
    }

    public ContentType getType() {
        if(this.type == null) {
            this.type = ContentType.JSON;
        }

        return this.type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public boolean isFound() {
        return this.found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public boolean isSlience() {
        return this.slience;
    }

    public void setSlience(boolean slience) {
        this.slience = slience;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public boolean isRealSlience() {
        return this.realSlience;
    }

    public void setRealSlience(boolean realSlience) {
        this.realSlience = realSlience;
    }

    public boolean isSlave() {
        return this.slave;
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }
}
