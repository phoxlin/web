package com.core.server.ms;

import com.core.enuts.ColumnType;
import com.core.server.Action;
import com.core.server.c.DType;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PageColumn implements Serializable{
    private static final long serialVersionUID = 1L;
    private Map<String, String> m = new HashMap();
    private String name;
    private String display;
    private ColumnType columnType;
    private String controlType;
    private String width;
    private String height;
    private boolean hidden;
    private boolean readonly;
    private boolean unique;
    private boolean oneLine;
    private boolean nullable;
    private int spanColNum;
    private String defaultValue;
    private String bindType;
    private String bindData;
    private int min = 0;
    private int max = -1;
    private int precision = 0;
    private int sort;

    public PageColumn() {
    }

    public JSONObject toJson(Action act, DType dType, JSONObject oo) throws Exception {
        JSONObject o = new JSONObject();
        o.put("name", this.getName());
        o.put("display", this.getDisplay());
        o.put("columnType", ("" + this.getColumnType()).toLowerCase());
        o.put("controlType", this.getControlType());
        o.put("width", this.getWidth());
        o.put("height", this.getHeight());
        o.put("hidden", this.isHidden());
        o.put("readOnly", this.isReadonly());
        o.put("unique", this.isUnique());
        o.put("oneLine", this.isOneLine());
        o.put("nullable", this.isNullable());
        o.put("spanColNum", this.getSpanColNum());
        o.put("defaultValue", this.getDefaultValue());
        o.put("sort", this.getSort());
        o.put("min", this.getMin());
        o.put("max", this.getMax());
        o.put("precision", this.getPrecision());

        String sql;
        try {
            sql = oo.get(this.getName()).toString();
            if(sql != null && sql.length() > 0) {
                o.put("defaultValue", sql);
            }
        } catch (Exception var10) {
            ;
        }

        Iterator matcher = this.getM().entrySet().iterator();

        while(matcher.hasNext()) {
            Map.Entry sql1 = (Map.Entry)matcher.next();
            o.put((String)sql1.getKey(), sql1.getValue());
        }

        Code sql2;
        if("codetable".equalsIgnoreCase(this.bindType)) {
            if(this.bindData != null && this.bindData.length() > 0) {
                sql2 = Codes.code(this.bindData);
                sql2.toListMap();
                o.put("bindData", sql2.toJsonArray());
            }
        } else if("treecodetable".equalsIgnoreCase(this.bindType)) {
            if(this.bindData != null && this.bindData.length() > 0) {
                sql2 = Codes.code(this.bindData);
                sql2.toListMap();
                o.put("bindData", sql2.toJsonArray());
            }
        } else if(("csql".equalsIgnoreCase(this.bindType) || "tsql".equalsIgnoreCase(this.bindType) || "sql".equalsIgnoreCase(this.bindType)) && this.bindData != null && this.bindData.length() > 0) {
            sql = this.bindData;
            Matcher matcher1 = MsInfo.pattern.matcher(sql);

            while(matcher1.find()) {
                String code = matcher1.group(1);

                try {
                    String val = BeanUtils.getProperty(act.getSessionUser(), code);
                    sql = sql.replace("#{user." + code + "}", val);
                } catch (Exception var9) {
                    ;
                }
            }

            Code code1 = Codes.sql(sql, act.getConnection());
            code1.toListMap();
            o.put("bindData", code1.toJsonArray());
        }

        return o;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getControlType() {
        return this.controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isOneLine() {
        return this.oneLine;
    }

    public void setOneLine(boolean oneLine) {
        this.oneLine = oneLine;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getSpanColNum() {
        return this.spanColNum;
    }

    public void setSpanColNum(int spanColNum) {
        this.spanColNum = spanColNum;
    }

    public String getDefaultValue() {
        if(this.defaultValue == null) {
            this.defaultValue = "";
        }

        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public ColumnType getColumnType() {
        return this.columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public String getBindType() {
        return this.bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getBindData() {
        return this.bindData;
    }

    public void setBindData(String bindData) {
        this.bindData = bindData;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public Map<String, String> getM() {
        return this.m;
    }
}
