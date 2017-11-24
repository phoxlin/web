package com.core.server.ms;

import com.core.server.Action;
import com.core.server.c.DType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PageColumns implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<PageColumn> cols = new ArrayList();
    private MsInfo msInfo;

    public PageColumns() {
    }

    public JSONArray toJson(Action act, DType dType, JSONObject o) throws Exception {
        JSONArray list = new JSONArray();
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            PageColumn col = (PageColumn)this.cols.get(i);
            JSONObject obj = col.toJson(act, dType, o);
            list.put(obj);
        }

        return list;
    }

    public void addColumn(PageColumn col) {
        this.cols.add(col);
    }

    public List<PageColumn> getCols() {
        return this.cols;
    }

    public MsInfo getMsInfo() {
        return this.msInfo;
    }

    public void setMsInfo(MsInfo msInfo) {
        this.msInfo = msInfo;
    }
}
