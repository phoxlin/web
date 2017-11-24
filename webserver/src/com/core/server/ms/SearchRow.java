package com.core.server.ms;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class SearchRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private int rownum = 1;
    private int colnum = 1;
    private String label;
    private String columnname;
    private String compare = "eq";
    private String columnvalue;

    public SearchRow() {
    }

    public SearchRow(int rownum, int colnum, String label, String columnname, String compare, String columnvalue) {
        this.rownum = rownum;
        this.colnum = colnum;
        this.label = label;
        this.columnname = columnname;
        this.compare = compare;
        this.columnvalue = columnvalue;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("colnum", this.colnum > 0?Integer.valueOf(this.colnum):"0");
        obj.put("rownum", this.rownum > 0?Integer.valueOf(this.rownum):"0");
        obj.put("label", this.label);
        obj.put("columnname", this.columnname);
        obj.put("compare", this.compare);
        obj.put("columnvalue", this.columnvalue != null && this.columnvalue.length() > 0?this.columnvalue:"");
        return obj;
    }

    public int getRownum() {
        return this.rownum;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColumnname() {
        return this.columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    public String getCompare() {
        return this.compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getColumnvalue() {
        return this.columnvalue;
    }

    public void setColumnvalue(String columnvalue) {
        this.columnvalue = columnvalue;
    }

    public int getColnum() {
        return this.colnum;
    }

    public void setColnum(int colnum) {
        this.colnum = colnum;
    }
}
