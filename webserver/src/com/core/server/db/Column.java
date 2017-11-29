package com.core.server.db;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.server.c.Code;
import com.core.server.c.Codes;
import com.core.server.log.JhLog;
import com.core.server.log.Logger;
import com.core.server.ms.MsInfo;
import com.core.server.task.QmInfo;
import com.core.server.tools.Utils;
import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by Administrator on 2017/11/24.
 */
public class Column implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String field;
    private boolean isKey;
    private ColumnType type;
    private String format;
    private boolean isAutoGenerate;
    private Object value;
    private boolean nullable;
    private boolean isNull = false;
    private int length;
    private String comment;
    private String controlType;
    private String input_tablename;
    private int width;
    private int min;
    private int max;
    private int decamial;
    private String bindType;
    private String bindData;
    private String otherset;
    private String placeholder;
    private boolean show;
    private boolean sort;
    private boolean ignore;
    private boolean edit;
    private boolean hidden;
    private boolean readonly;
    private boolean line;
    private boolean query;
    private String defaultValue;
    private String col_class;
    private String col_style;
    private int cols = 6;
    private int labelCols = 4;
    private int fieldCols = 6;
    private int inputHeight = 50;
    private int inputSpanCol = 1;
    private String search_compare;
    Map<String, String> map = new HashMap();

    public Column() {
    }

    public String getComment() {
        if(this.comment == null) {
            this.comment = this.getName().toUpperCase();
        }

        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public JSONObject toJsonConf(User user) {
        JSONObject o = new JSONObject();
        Iterator var4 = this.map.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry e = (Map.Entry)var4.next();
            o.put((String)e.getKey(), e.getValue());
        }

        o.put("name", this.getName());
        o.put("comment", this.getComment());
        o.put("controlType", this.getControlType());
        o.put("width", this.width);
        o.put("length", this.length);
        o.put("input_tablename", this.getInput_tablename());
        o.put("placeholder", this.getPlaceholder());
        o.put("format", this.getFormat());
        o.put("decamial", this.decamial);
        o.put("min", this.min);
        o.put("max", this.max);
        o.put("bindType", this.getBindType());
        o.put("bindData", this.getBindData());
        o.put("otherset", this.getOtherset());
        o.put("hidden", this.isHidden());
        o.put("col_class", this.col_class);
        o.put("col_style", this.col_style);
        o.put("cols", this.cols);
        o.put("oneline", this.line);
        o.put("labelCols", this.labelCols);
        o.put("fieldCols", this.fieldCols);
        o.put("defaultValue", this.getDefaultValue());
        o.put("nullable", this.isNullable());

        try {
            o.put("options", this.getOptions(user));
        } catch (Exception var5) {
            Logger.error(var5);
        }

        return o;
    }

    public Code getCode(User user, JhLog L) {
        if(Utils.contains(QmInfo.allCodes, this.getBindType()) && this.getBindData() != null && this.getBindData().length() > 0) {
            Code code = null;
            if(Utils.contains(QmInfo.hasValuedCodes, this.getBindType())) {
                try {
                    code = Codes.code(this.getBindData());
                    return code;
                } catch (Exception var13) {
                    L.error(var13);
                }
            } else if(Utils.contains(QmInfo.sqlCodes, this.getBindType())) {
                String sql = this.getBindData();
                code = (Code)QmInfo.queryedData.get(sql);
                long now = System.currentTimeMillis();
                if(code != null) {
                    long trimSql = 0L;

                    try {
                        trimSql = ((Long)QmInfo.queryedDataTime.get(sql)).longValue();
                    } catch (Exception var12) {
                        ;
                    }

                    if(now - trimSql > 60000L) {
                        code = null;
                    }
                }

                if(code == null) {
                    String trimSql1 = new String(sql);
                    Matcher matcher = MsInfo.pattern.matcher(sql);

                    while(matcher.find()) {
                        String word = matcher.group(1);

                        try {
                            String uVal = BeanUtils.getProperty(user, word);
                            trimSql1 = sql.replace("#{user." + word + "}", uVal);
                        } catch (Exception var11) {
                            ;
                        }
                    }

                    code = Codes.sql(trimSql1);
                    QmInfo.queryedData.put(sql, code);
                    QmInfo.queryedDataTime.put(sql, Long.valueOf(now));
                }

                return code;
            }
        }

        return null;
    }

    private JSONArray getOptions(User user) throws Exception {
        JhLog L = new JhLog();
        Code code = this.getCode(user, L);
        return code != null?code.toJsonArray():new JSONArray();
    }

    public Date getDateValue(Object obj) {
        Date temp = null;

        try {
            if(obj != null) {
                if(obj instanceof Date) {
                    temp = (Date)obj;
                } else if(obj instanceof java.util.Date) {
                    temp = new Date(((java.util.Date)obj).getTime());
                } else if(obj instanceof Timestamp) {
                    temp = new Date(((Timestamp)obj).getTime());
                } else if(obj instanceof DATE) {
                    temp = new Date(((DATE)obj).dateValue().getTime());
                } else if(obj instanceof TIMESTAMP) {
                    try {
                        temp = new Date(((TIMESTAMP)obj).dateValue().getTime());
                    } catch (SQLException var6) {
                        temp = null;
                    }
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(this.getFormat());

                    try {
                        temp = (Date)sdf.parse(String.valueOf(obj));
                    } catch (Exception var5) {
                        sdf = new SimpleDateFormat("yyyyMMdd");
                        temp = (Date)sdf.parse(String.valueOf(obj));
                    }
                }
            } else {
                temp = null;
            }
        } catch (Exception var7) {
            ;
        }

        return temp;
    }

    public Date getDateValue(Map<String, Object> map) {
        Object obj = map.get(this.name);
        return this.getDateValue((Object)obj);
    }

    public String toString() {
        return this.name + ":" + this.type + ":" + this.nullable + ":" + this.value;
    }

    public int getLength() {
        return this.length;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isKey() {
        return this.isKey;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public ColumnType getType() {
        return this.type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public String getFormat() {
        if(this.format == null) {
            this.format = "";
        }

        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isAutoGenerate() {
        return this.isAutoGenerate;
    }

    public void setAutoGenerate(boolean isAutoGenerate) {
        this.isAutoGenerate = isAutoGenerate;
    }

    public Object getValue() {
        return this.value;
    }

    public String getValue(Object val) {
        if(val != null) {
            if(this.type != ColumnType.DATE && this.type != ColumnType.DATETIME) {
                return val.toString();
            } else {
                Date date = this.getDateValue((Object)val);
                return date != null?Utils.parseData(date, this.getFormat()):"";
            }
        } else {
            return "";
        }
    }

    public void setNullValue() throws Exception {
        this.isNull = true;
        this.value = null;
    }

    public void setValue(Object value) throws Exception {
        if(value != null && value.toString().trim().length() > 0) {
            if(this.getType() != ColumnType.STRING && this.getType() != ColumnType.TEXT && this.getType() != ColumnType.MEDIUMTEXT && this.getType() != ColumnType.LONGTEXT) {
                SimpleDateFormat c;
                if(this.getType() == ColumnType.DATE) {
                    if(value instanceof Date) {
                        this.value = (Date)value;
                    } else if(value instanceof java.util.Date) {
                        this.value = new Date(((java.util.Date)value).getTime());
                    } else if(value instanceof Timestamp) {
                        this.value = new Date(((Timestamp)value).getTime());
                    } else if(value instanceof String) {
                        c = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            this.value = new Date(c.parse(value.toString()).getTime());
                        } catch (ParseException var10) {
                            throw new Exception("Set entity value error: invalid Date value:" + value);
                        }
                    } else {
                        try {
                            c = new SimpleDateFormat("yyyy-MM-dd");
                            this.value = new Date(c.parse(value.toString()).getTime());
                        } catch (Exception var9) {
                            throw new Exception("Set entity value error: invalid Date value:" + value);
                        }
                    }
                } else if(this.getType() == ColumnType.DATETIME) {
                    if(value instanceof Date) {
                        this.value = new Timestamp(((Date)value).getTime());
                    } else if(value instanceof java.util.Date) {
                        this.value = new Timestamp(((java.util.Date)value).getTime());
                    } else if(value instanceof Timestamp) {
                        this.value = (Timestamp)value;
                    } else if(value instanceof String) {
                        c = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if(value.toString().length() == 10) {
                            value = value + " 00:00:00";
                        }

                        try {
                            this.value = new Timestamp(c.parse(value.toString()).getTime());
                        } catch (ParseException var8) {
                            throw new Exception("Set entity value error: invalid Date value:" + value);
                        }
                    } else {
                        if(value.toString().length() == 10) {
                            value = value + " 00:00:00";
                        }

                        c = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try {
                            this.value = new Timestamp(c.parse(value.toString()).getTime());
                        } catch (ParseException var7) {
                            throw new Exception("Set entity value error: invalid Date value:" + value);
                        }
                    }
                } else {
                    Double c1;
                    if(this.getType() == ColumnType.INT) {
                        if(value instanceof Integer) {
                            this.value = (Integer)value;
                        } else {
                            try {
                                c1 = Double.valueOf(Double.parseDouble(String.valueOf(value)));
                                this.value = Integer.valueOf(c1.intValue());
                            } catch (Exception var6) {
                                throw new Exception("Set entity value error: invalid int value:" + value);
                            }
                        }
                    } else if(this.getType() == ColumnType.LONG) {
                        if(value instanceof Long) {
                            this.value = (Long)value;
                        } else {
                            try {
                                c1 = Double.valueOf(Double.parseDouble(String.valueOf(value)));
                                this.value = Long.valueOf(c1.longValue());
                            } catch (Exception var5) {
                                throw new Exception("Set entity value error: invalid long value:" + value);
                            }
                        }
                    } else if(this.getType() == ColumnType.FLOAT) {
                        if(value instanceof Float) {
                            this.value = (Float)value;
                        } else {
                            try {
                                this.value = Float.valueOf(Float.parseFloat(String.valueOf(value)));
                            } catch (Exception var4) {
                                throw new Exception("Set entity value error: invalid float value:" + value);
                            }
                        }
                    } else if(this.getType() == ColumnType.BLOB) {
                        if(!(value instanceof Blob)) {
                            throw new Exception("Set entity value error: invalid blob value:" + value);
                        }

                        this.value = value;
                    } else if(this.getType() == ColumnType.CLOB) {
                        if(value instanceof Clob) {
                            Clob c2 = (Clob)value;
                            this.value = c2.getSubString(1L, Integer.parseInt(String.valueOf(c2.length())));
                        } else {
                            if(!(value instanceof String)) {
                                throw new Exception("Set entity value error: invalid clob value:" + value);
                            }

                            this.value = value;
                        }
                    }
                }
            } else {
                this.value = value.toString();
            }
        } else {
            this.value = null;
        }

    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNull() {
        return this.isNull;
    }

    public String getFormatStringValue() {
        if(this.value != null) {
            SimpleDateFormat sf = new SimpleDateFormat(this.format);
            return sf.format(this.value);
        } else {
            return "";
        }
    }

    public String getControlType() {
        if(this.controlType == null) {
            this.controlType = "easyui-validatebox";
        }

        return this.controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getInput_tablename() {
        if(this.input_tablename == null) {
            this.input_tablename = "";
        }

        return this.input_tablename;
    }

    public void setInput_tablename(String input_tablename) {
        this.input_tablename = input_tablename;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(String min) {
        try {
            this.min = Integer.parseInt(min);
        } catch (Exception var3) {
            this.min = 0;
        }

    }

    public int getMax() {
        return this.max;
    }

    public void setMax(String max) {
        try {
            this.max = Integer.parseInt(max);
        } catch (Exception var3) {
            this.max = 10000;
        }

    }

    public int getDecamial() {
        return this.decamial;
    }

    public void setDecamial(int decamial) {
        this.decamial = decamial;
    }

    public String getBindType() {
        if(this.bindType == null) {
            this.bindType = "";
        }

        return this.bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getBindData() {
        if(this.bindData == null) {
            this.bindData = "";
        }

        return this.bindData;
    }

    public void setBindData(String bindData) {
        this.bindData = bindData;
    }

    public String getOtherset() {
        if(this.otherset == null) {
            this.otherset = "";
        }

        return this.otherset;
    }

    public void setOtherset(String otherset) {
        this.otherset = otherset;
    }

    public void setNull(boolean isNull) {
        this.isNull = isNull;
    }

    public int getWidth() {
        return this.width;
    }

    public String getPlaceholder() {
        if(this.placeholder == null) {
            this.placeholder = "";
        }

        return this.placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setWidth(String width) {
        try {
            this.width = Integer.parseInt(width);
        } catch (Exception var3) {
            this.width = 100;
        }

    }

    public void setDecamial(String decamial) {
        try {
            this.decamial = Integer.parseInt(decamial);
        } catch (Exception var3) {
            this.decamial = 0;
        }

    }

    public void setLength(String length) {
        try {
            this.length = Integer.parseInt(length);
        } catch (Exception var3) {
            ;
        }

    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isSort() {
        return this.sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public boolean isIgnore() {
        return this.ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isEdit() {
        return this.edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
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

    public boolean isLine() {
        return this.line;
    }

    public void setLine(boolean line) {
        this.line = line;
    }

    public boolean isQuery() {
        return this.query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getCol_class() {
        return this.col_class;
    }

    public void setCol_class(String col_class) {
        this.col_class = col_class;
    }

    public String getCol_style() {
        return this.col_style;
    }

    public void setCol_style(String col_style) {
        this.col_style = col_style;
    }

    public int getCols() {
        return this.cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getLabelCols() {
        return this.labelCols;
    }

    public void setLabelCols(int labelCols) {
        this.labelCols = labelCols;
    }

    public int getFieldCols() {
        return this.fieldCols;
    }

    public void setFieldCols(int fieldCols) {
        this.fieldCols = fieldCols;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getInputHeight() {
        return this.inputHeight;
    }

    public void setInputHeight(int inputHeight) {
        this.inputHeight = inputHeight;
    }

    public int getInputSpanCol() {
        return this.inputSpanCol;
    }

    public void setInputSpanCol(int inputSpanCol) {
        this.inputSpanCol = inputSpanCol;
    }

    public String getSearch_compare() {
        return this.search_compare;
    }

    public void setSearch_compare(String search_compare) {
        this.search_compare = search_compare;
    }

    public Map<String, String> getMap() {
        return this.map;
    }
}
