package com.core.server.ms;

import com.core.server.Action;
import com.core.server.c.DType;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PageLegend implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String title;
    private PageColumns pageColumns = new PageColumns();
    private MsInfo msInfo;

    public PageLegend() {
    }

    public JSONObject toJson(Action act, DType dType, JSONObject o) throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", this.getName());
        json.put("title", this.getTitle());
        json.put("controls", this.pageColumns.toJson(act, dType, o));
        return json;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PageColumns getPageColumns() {
        return this.pageColumns;
    }

    public void setPageColumns(PageColumns pageColumns) {
        this.pageColumns = pageColumns;
    }

    public MsInfo getMsInfo() {
        return this.msInfo;
    }

    public void setMsInfo(MsInfo msInfo) {
        this.msInfo = msInfo;
    }
}
