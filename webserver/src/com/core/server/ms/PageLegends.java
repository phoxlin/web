package com.core.server.ms;

import com.core.server.Action;
import com.core.server.c.DType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PageLegends implements Serializable{
    private static final long serialVersionUID = 1L;
    private int columnNumber = 2;
    private List<PageLegend> legends = new ArrayList();
    private MsInfo msInfo;

    public PageLegends() {
    }

    public JSONArray toJson(Action act, DType dType, String _id) throws Exception {
        Object o = null;
        JSONArray list = new JSONArray();
        int i = 0;

        for(int l = this.legends.size(); i < l; ++i) {
            PageLegend pageLegend = (PageLegend)this.legends.get(i);
            JSONObject obj = pageLegend.toJson(act, dType, (JSONObject)o);
            list.put(obj);
        }

        return list;
    }

    public JSONArray toJson(Action act, DType dType) throws Exception {
        JSONObject o = new JSONObject();
        JSONArray list = new JSONArray();
        Iterator var6 = this.legends.iterator();

        while(var6.hasNext()) {
            PageLegend legend = (PageLegend)var6.next();
            JSONObject obj = legend.toJson(act, dType, o);
            list.put(obj);
        }

        return list;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public List<PageLegend> getCols() {
        return this.legends;
    }

    public void addLegend(PageLegend legend) {
        this.legends.add(legend);
    }

    public MsInfo getMsInfo() {
        return this.msInfo;
    }

    public void setMsInfo(MsInfo msInfo) {
        this.msInfo = msInfo;
    }
}
