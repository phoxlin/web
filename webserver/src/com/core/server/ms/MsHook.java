package com.core.server.ms;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public abstract class MsHook {
    private Connection conn;
    private int pagesize;
    private int currentPage;
    private boolean paging;
    private User user;
    private MsInfo ms;
    private PageAction pageAction;
    private long totalsize;

    public MsHook() {
    }

    public MsInfo getMs() {
        return this.ms;
    }

    public void setMs(MsInfo ms) {
        this.ms = ms;
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

    public long getTotalsize() {
        return this.totalsize;
    }

    public void setTotalsize(long totalsize) {
        this.totalsize = totalsize;
    }

    public PageAction getPageAction() {
        return this.pageAction;
    }

    public void setPageAction(PageAction pageAction) {
        this.pageAction = pageAction;
    }
}
