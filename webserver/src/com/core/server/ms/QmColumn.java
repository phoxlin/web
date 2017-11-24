package com.core.server.ms;

import com.core.enuts.ColumnType;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QmColumn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String fieldName;
    private String display;
    private ColumnType type;
    private int width;
    private boolean show;
    private String format;
    private boolean send;
    private String reminder;
    private String bindtype;
    private String binddata;
    private QmAlign align;
    private boolean sort;
    private boolean ignore;
    private boolean nullable;

    public QmColumn() {
    }

    public String getControlType() {
        return !"codetable".equalsIgnoreCase(this.bindtype) && !"mgo".equalsIgnoreCase(this.bindtype) && !"cmgo".equalsIgnoreCase(this.bindtype) && !"csql".equalsIgnoreCase(this.bindtype) && !"sql".equalsIgnoreCase(this.bindtype)?(!"tsql".equalsIgnoreCase(this.bindtype) && !"tmgo".equalsIgnoreCase(this.bindtype)?(this.type == ColumnType.DATE?"easyui-datebox":(this.type == ColumnType.DATETIME?"easyui-datetimebox":(this.type != ColumnType.CLOB && this.type != ColumnType.LONGTEXT && this.type != ColumnType.MEDIUMTEXT && this.type != ColumnType.TEXT?"easyui-validatebox":"textarea"))):"easyui-combotree"):"easyui-combobox";
    }

    public Date getDateValue(Map<String, Object> map) {
        Date temp = null;

        try {
            Object obj = map.get(this.code);
            if(obj != null) {
                if(obj instanceof Date) {
                    temp = (Date)obj;
                } else if(obj instanceof Date) {
                    temp = new Date(((Date)obj).getTime());
                } else if(obj instanceof Timestamp) {
                    temp = new Date(((Timestamp)obj).getTime());
                } else if(obj instanceof DATE) {
                    temp = new Date(((DATE)obj).dateValue().getTime());
                } else if(obj instanceof TIMESTAMP) {
                    try {
                        temp = new Date(((TIMESTAMP)obj).dateValue().getTime());
                    } catch (SQLException var7) {
                        temp = null;
                    }
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        temp = sdf.parse(String.valueOf(obj));
                    } catch (Exception var6) {
                        sdf = new SimpleDateFormat("yyyyMMdd");
                        temp = sdf.parse(String.valueOf(obj));
                    }
                }
            } else {
                temp = null;
            }
        } catch (Exception var8) {
            ;
        }

        return temp;
    }

    public String getValue(Map<String, Object> map) {
        String value = Utils.getMapStringValue(map, this.getCode(), "");
        if(this.format != null && this.format.length() > 0) {
            SimpleDateFormat sdf;
            Date val;
            if(ColumnType.DATE == this.type) {
                sdf = new SimpleDateFormat(this.format);
                val = this.getDateValue(map);
                return val != null?sdf.format(val):"";
            } else if(ColumnType.DATETIME == this.type) {
                sdf = new SimpleDateFormat(this.format);
                val = this.getDateValue(map);
                return val != null?sdf.format(val):"";
            } else {
                return String.format(this.format, new Object[]{value});
            }
        } else {
            return value;
        }
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public ColumnType getType() {
        return this.type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isSend() {
        return this.send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getReminder() {
        return this.reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public QmAlign getAlign() {
        return this.align;
    }

    public void setAlign(QmAlign align) {
        this.align = align;
    }

    public String getStringValue(Object v) {
        String sv = "";
        if(v != null) {
            if(this.type == ColumnType.FLOAT && this.format != null) {
                DecimalFormat sf1 = new DecimalFormat(this.format);
                sv = sf1.format(v);
            } else {
                SimpleDateFormat sf;
                if(this.type == ColumnType.DATE) {
                    sf = new SimpleDateFormat("yyyy-MM-dd");
                    if(this.format != null) {
                        sf = new SimpleDateFormat(this.format);
                    }

                    sv = sf.format(v);
                } else if(this.type == ColumnType.DATETIME) {
                    sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(this.format != null) {
                        sf = new SimpleDateFormat(this.format);
                    }

                    sv = sf.format(v);
                } else {
                    sv = String.valueOf(v);
                }
            }
        }

        return sv;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getBindtype() {
        return this.bindtype;
    }

    public void setBindtype(String bindtype) {
        this.bindtype = bindtype;
    }

    public String getBinddata() {
        return this.binddata;
    }

    public void setBinddata(String binddata) {
        this.binddata = binddata;
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

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
