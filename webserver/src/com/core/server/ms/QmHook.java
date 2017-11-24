package com.core.server.ms;

import com.core.User;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public abstract class QmHook {
    private List<Map<String, Object>> rawResultSet;
    private Connection conn;
    private int pagesize;
    private int currentPage;
    private boolean paging;
    private User user;
    public MsInfo qmInfo;
    private Map<String, Object> p = null;

    public QmHook() {
    }

    public Map<String, Object> getP() {
        if(this.p == null) {
            this.p = new HashMap();
        }

        return this.p;
    }

    public void setP(Map<String, Object> p) {
        this.p = p;
    }

    public void setRawResultSet(List<Map<String, Object>> rawResultSet) {
        this.rawResultSet = rawResultSet;
    }

    public List<Map<String, Object>> getRawResultSet() {
        return this.rawResultSet;
    }

    public abstract List<Map<String, Object>> getTrimedSet() throws Exception;

    public Connection getConn() {
        return this.conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public int getPagesize() {
        return this.pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isPaging() {
        return this.paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
