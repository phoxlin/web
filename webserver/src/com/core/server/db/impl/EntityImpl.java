package com.core.server.db.impl;

import com.core.enuts.ColumnType;
import com.core.enuts.DBType;
import com.core.server.Action;
import com.core.server.db.Column;
import com.core.server.db.DBUtils;
import com.core.server.db.Entity;
import com.core.server.log.JhLog;
import com.core.server.ms.MsInfo;
import com.core.server.tools.Utils;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sql.rowset.serial.SerialClob;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class EntityImpl implements Entity {
    public JhLog L = null;
    private static final long serialVersionUID = 1L;
    private String tablename;
    private String tablenameBak;
    private String tableComment;
    private Connection conn;
    private List<Column> cols = new ArrayList();
    private List<Map<String, Object>> values = new ArrayList();
    private int maxResultCount = 0;
    private boolean slience = false;
    private String condition;
    private boolean slice = false;

    public static void main(String[] args) throws SQLException {
        DBM db = new DBM();
        Connection conn = null;

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            EntityImpl e = new EntityImpl("sys_user", conn);
            e.setValue("id", "586468544c6550110005fea6");
            e.setValue("area_code", "100");
            e.setNullValue("org_code");
            e.update();
            conn.commit();
        } catch (Exception var7) {
            conn.rollback();
            var7.printStackTrace();
        } finally {
            db.freeConnection(conn);
        }

    }

    public EntityImpl(Connection conn) {
        this.conn = conn;
        this.L = new JhLog();
    }

    public EntityImpl(Connection conn, JhLog L) {
        this.conn = conn;
        this.L = L;
    }

    public EntityImpl(String name, Connection conn) throws Exception {
        this.L = new JhLog();
        this.initialize(name, (Connection)conn);
    }

    public EntityImpl(String name, Connection conn, JhLog L) throws Exception {
        this.L = L;
        this.initialize(name, (Connection)conn);
    }

    public EntityImpl(Action action) throws Exception {
        this.L = action.L;
        this.conn = action.getConnection();
    }

    public EntityImpl(String name, Action action) throws Exception {
        this.initialize(name, (Action)action);
    }

    public void initialize(String name, Connection conn) throws Exception {
        name = name.toLowerCase();
        this.conn = conn;
        this.clear();
        this.L.debug("Initialize Entity(" + name + ")..........");
        File file = null;
        if(NettyUtils.getRootContent().contains("WEB-INF")) {
            file = MsInfo.getFile(new File(NettyUtils.getRootContent() + "/configures/database/rdb"), name, "xml");
        } else {
            file = MsInfo.getFile(new File(Resources.getProperty("workspace") + "WEB-INF/configures/database/rdb"), name, "xml");
        }

        if(file == null) {
            this.tablename = name;
            throw new Exception("Initialize Entity[" + name + "] failed:Can\'t find DB config file:" + name + ".xml");
        } else {
            this.L.debug("Found config file for Entity(" + name + ")..........");
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);
            Element root = doc.getRootElement();
            this.tablename = root.attributeValue("name");
            if(this.tablename != null && this.tablename.trim().length() > 0) {
                this.tablenameBak = this.tablename;
                this.tableComment = root.attributeValue("comment");
                if(this.tableComment == null || this.tableComment.length() <= 0) {
                    this.tableComment = this.tablename.toUpperCase();
                }

                this.slice = Utils.isTrue(root.attributeValue("slice"));
                List columns = doc.selectNodes("/table/column");
                if(columns == null || columns.size() <= 0) {
                    columns = doc.selectNodes("/table/columns/column");
                }

                if(columns != null && columns.size() > 0) {
                    boolean hasKey = false;
                    int i = 0;
                    int l = columns.size();

                    while(i < l) {
                        Element ele = (Element)columns.get(i);
                        String _name = ele.attributeValue("name");
                        String field = ele.attributeValue("field");
                        String type = ele.attributeValue("type");
                        String iskey = ele.attributeValue("iskey");
                        String format = ele.attributeValue("format");
                        String isAutoGenerate = ele.attributeValue("isAutoGenerate");
                        String nullable = ele.attributeValue("nullable");
                        String comment = ele.attributeValue("comment");
                        String length = ele.attributeValue("length");
                        Column col = new Column();
                        col.setComment(comment);
                        col.setLength(length);
                        if(_name != null && _name.trim().length() > 0) {
                            col.setName(_name.trim().toLowerCase());
                            if(field != null && field.trim().length() > 0) {
                                col.setField(field.trim().toLowerCase());
                                if(type != null && type.trim().length() > 0) {
                                    if(type.equalsIgnoreCase("int")) {
                                        col.setType(ColumnType.INT);
                                    } else if(type.equalsIgnoreCase("long")) {
                                        col.setType(ColumnType.LONG);
                                    } else if(type.equalsIgnoreCase("bigint")) {
                                        col.setType(ColumnType.LONG);
                                    } else if(type.equalsIgnoreCase("float")) {
                                        col.setType(ColumnType.FLOAT);
                                    } else if(type.equalsIgnoreCase("date")) {
                                        col.setType(ColumnType.DATE);
                                    } else if(type.equalsIgnoreCase("datetime")) {
                                        col.setType(ColumnType.DATETIME);
                                    } else if(type.equalsIgnoreCase("blob")) {
                                        col.setType(ColumnType.BLOB);
                                    } else if(type.equalsIgnoreCase("clob")) {
                                        col.setType(ColumnType.CLOB);
                                    } else if(type.equalsIgnoreCase("text")) {
                                        col.setType(ColumnType.TEXT);
                                    } else if(type.equalsIgnoreCase("mediumtext")) {
                                        col.setType(ColumnType.MEDIUMTEXT);
                                    } else if(type.equalsIgnoreCase("longtext")) {
                                        col.setType(ColumnType.LONGTEXT);
                                    } else {
                                        col.setType(ColumnType.STRING);
                                    }
                                } else {
                                    col.setType(ColumnType.STRING);
                                }

                                if(iskey != null && iskey.trim().equalsIgnoreCase("true")) {
                                    col.setKey(true);
                                    hasKey = true;
                                } else {
                                    col.setKey(false);
                                }

                                if(isAutoGenerate != null && isAutoGenerate.trim().equalsIgnoreCase("true")) {
                                    col.setAutoGenerate(true);
                                } else {
                                    col.setAutoGenerate(false);
                                }

                                if(format != null && format.trim().length() > 0) {
                                    col.setFormat(format.trim());
                                }

                                if("false".equalsIgnoreCase(nullable)) {
                                    col.setNullable(false);
                                } else {
                                    col.setNullable(true);
                                }

                                String controlType = ele.attributeValue("controlType");
                                String input_tablename = ele.attributeValue("input_tablename");
                                String width = ele.attributeValue("width");
                                String min = ele.attributeValue("min");
                                String max = ele.attributeValue("max");
                                String decamial = ele.attributeValue("decamial");
                                String bindType = ele.attributeValue("bindType");
                                String bindData = ele.attributeValue("bindData");
                                String otherset = ele.attributeValue("otherset");
                                String placeholder = ele.attributeValue("placeholder");
                                col.setControlType(controlType);
                                if(input_tablename != null && input_tablename.length() > 0) {
                                    col.setInput_tablename(input_tablename);
                                } else {
                                    col.setInput_tablename(this.tablename);
                                }

                                col.setWidth(width);
                                col.setMin(min);
                                col.setMax(max);
                                col.setDecamial(decamial);
                                if(bindType != null && bindType.length() > 0) {
                                    col.setBindType(bindType);
                                }

                                if(bindData != null && bindData.length() > 0) {
                                    col.setBindData(bindData);
                                }

                                if(otherset != null && otherset.length() > 0) {
                                    col.setOtherset(otherset);
                                }

                                if(placeholder != null && placeholder.length() > 0) {
                                    col.setPlaceholder(placeholder);
                                }

                                this.cols.add(col);
                                ++i;
                                continue;
                            }

                            throw new Exception("Initialize Entity[" + name + "] failed:Invalid \'field\' attribute for \'column\' Element, DB config file:" + file.getName());
                        }

                        throw new Exception("Initialize Entity[" + name + "] failed:Invalid \'name\' attribute for \'column\' Element, DB config file:" + file.getName());
                    }

                    if(!hasKey) {
                        throw new Exception("Initialize Entity[" + name + "] failed:No primary key setting for DB config file:" + file.getName());
                    } else {
                        reader = null;
                        doc = null;
                        root = null;
                    }
                } else {
                    throw new Exception("Initialize Entity[" + name + "] failed:No \'column\' properties for DB config file:" + file.getName());
                }
            } else {
                throw new Exception("Initialize Entity[" + name + "] failed:Invalid \'name\' attribute for DB config file:" + file.getName());
            }
        }
    }

    public void initialize(String name, Action action) throws Exception {
        if(action == null) {
            throw new Exception("Initialize Entity[" + name + "] failed:parameter \'action\' is null");
        } else {
            this.L = action.L;
            this.initialize(name, (Connection)action.getConnection());
        }
    }

    private void clear() throws Exception {
        this.values.clear();
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column c = (Column)this.cols.get(i);
            c.setValue((Object)null);
        }

        this.maxResultCount = 0;
    }

    public int searchOne() throws Exception {
        this.values.clear();
        String sql = this.createSearchSql();
        int size = this.doSearch(sql);
        this.maxResultCount = size;
        if(size > 0) {
            Map map = (Map)this.values.get(0);
            if(map != null) {
                for(int i = 0; i < this.cols.size(); ++i) {
                    Column col = (Column)this.cols.get(i);
                    Object value = map.get(col.getName());
                    if(value != null) {
                        col.setValue(value);
                    } else {
                        col.setValue((Object)null);
                    }
                }
            }
        }

        return size;
    }

    public String save(boolean updateAll) throws Exception {
        boolean create = true;
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column col = (Column)this.cols.get(i);
            Object value = col.getValue();
            if(col.isKey() && value != null && value.toString().trim().length() > 0) {
                create = false;
                break;
            }
        }

        if(create) {
            return this.create();
        } else {
            if(updateAll) {
                this.updateAll();
            } else {
                this.update();
            }

            return null;
        }
    }

    public String save() throws Exception {
        return this.save(false);
    }

    public String create() throws Exception {
        this.values.clear();
        long s = System.currentTimeMillis();
        if(!this.isSlience()) {
            this.L.debug("Start inserting Entity(" + this.tablename + ") into DB..........");
        }

        this.validate();
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(this.tablename);
        sql.append(" (");
        int oracleClob = 0;

        int id;
        for(id = this.cols.size(); oracleClob < id; ++oracleClob) {
            if(oracleClob != 0) {
                sql.append(" , ");
            }

            sql.append(((Column)this.cols.get(oracleClob)).getField());
        }

        sql.append(") values (");
        oracleClob = 0;

        for(id = this.cols.size(); oracleClob < id; ++oracleClob) {
            if(oracleClob != 0) {
                sql.append(" , ");
            }

            sql.append("?");
        }

        sql.append(")");
        HashMap var32 = new HashMap();
        String var33 = DBUtils.oid();
        if(!this.isSlience()) {
            this.L.info("Execute SQL(" + s + "):" + sql.toString());
        }

        PreparedStatement ps = this.conn.prepareStatement(sql.toString());
        int e = 0;

        Object ps2;
        for(int updateSql = this.cols.size(); e < updateSql; ++e) {
            Column es = (Column)this.cols.get(e);
            ps2 = es.getValue();
            if(es.isKey()) {
                if(!es.isAutoGenerate()) {
                    if(ps2 == null) {
                        throw new Exception("Entity[" + this.tablename + "] insert data failed for no primary key setting");
                    }

                    var33 = ps2.toString();
                } else if(ps2 != null) {
                    var33 = ps2.toString();
                }

                try {
                    ps.setString(e + 1, var33);
                    if(!this.isSlience()) {
                        this.L.debug("\tSQLParameter:" + (e + 1) + "=" + var33 + "," + es.getType());
                    }
                } catch (SQLException var31) {
                    throw new Exception("  SQLParameter:" + (e + 1) + "=" + var33 + "," + es.getType() + ", error:" + Utils.getErrorStack(var31));
                }
            } else if(es.getType() != ColumnType.STRING && es.getType() != ColumnType.TEXT && es.getType() != ColumnType.MEDIUMTEXT && es.getType() != ColumnType.LONGTEXT) {
                if(es.getType() == ColumnType.DATE) {
                    if(ps2 != null && ps2.toString().length() > 0) {
                        if(ps2 instanceof Date) {
                            try {
                                ps.setDate(e + 1, (Date)ps2);
                            } catch (SQLException var29) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var29));
                            }
                        } else {
                            if(!(ps2 instanceof java.util.Date)) {
                                throw new Exception("Entity[" + this.tablename + "] insert data failed for invalid Date value:" + ps2);
                            }

                            try {
                                ps.setDate(e + 1, new Date(((java.util.Date)ps2).getTime()));
                            } catch (SQLException var28) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var28));
                            }
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 91);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                } else if(es.getType() == ColumnType.DATETIME) {
                    if(ps2 != null && ps2.toString().length() > 0) {
                        if(ps2 instanceof Date) {
                            try {
                                ps.setTimestamp(e + 1, new Timestamp(((Date)ps2).getTime()));
                            } catch (SQLException var27) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var27));
                            }
                        } else if(ps2 instanceof java.util.Date) {
                            try {
                                ps.setTimestamp(e + 1, new Timestamp(((java.util.Date)ps2).getTime()));
                            } catch (SQLException var26) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var26));
                            }
                        } else {
                            if(!(ps2 instanceof Timestamp)) {
                                throw new Exception("Entity[" + this.tablename + "] insert data failed for invalid Date value:" + ps2);
                            }

                            try {
                                ps.setTimestamp(e + 1, (Timestamp)ps2);
                            } catch (SQLException var25) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var25));
                            }
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 93);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                } else if(es.getType() == ColumnType.INT) {
                    if(ps2 != null && ps2.toString().length() > 0) {
                        if(ps2 instanceof Integer) {
                            try {
                                ps.setInt(e + 1, ((Integer)ps2).intValue());
                            } catch (SQLException var24) {
                                throw new Exception(var24);
                            }
                        } else {
                            try {
                                ps.setInt(e + 1, Integer.parseInt(String.valueOf(ps2)));
                            } catch (Exception var23) {
                                throw new Exception("Entity[" + this.tablename + "] insert data failed for invalid int value:" + ps2);
                            }
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 4);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                } else if(es.getType() == ColumnType.LONG) {
                    if(ps2 != null && ps2.toString().length() > 0) {
                        if(ps2 instanceof Long) {
                            try {
                                ps.setLong(e + 1, ((Long)ps2).longValue());
                            } catch (SQLException var22) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var22));
                            }
                        } else {
                            try {
                                ps.setLong(e + 1, Long.parseLong(String.valueOf(ps2)));
                            } catch (Exception var21) {
                                throw new Exception("Entity[" + this.tablename + "] insert data failed for invalid long value:" + ps2);
                            }
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 4);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                } else if(es.getType() == ColumnType.FLOAT) {
                    if(ps2 != null && ps2.toString().length() > 0) {
                        if(ps2 instanceof Float) {
                            try {
                                ps.setFloat(e + 1, ((Float)ps2).floatValue());
                            } catch (SQLException var20) {
                                throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var20));
                            }
                        } else {
                            try {
                                ps.setFloat(e + 1, Float.parseFloat(String.valueOf(ps2)));
                            } catch (Exception var19) {
                                throw new Exception("Entity[" + this.tablename + "] insert data failed for invalid float value:" + ps2);
                            }
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 6);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                } else if(es.getType() == ColumnType.BLOB) {
                    if(ps2 != null) {
                        try {
                            ps.setBlob(e + 1, (Blob)ps2);
                        } catch (SQLException var18) {
                            throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var18));
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 2004);
                        this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                    }
                } else if(es.getType() == ColumnType.CLOB) {
                    if(ps2 != null) {
                        try {
                            if(DBUtils.getRDBType() == DBType.Oracle) {
                                if(ps2 instanceof String) {
                                    ps.setClob(e + 1, CLOB.getEmptyCLOB());
                                    var32.put(es, String.valueOf(ps2));
                                }
                            } else {
                                ps.setClob(e + 1, new SerialClob(String.valueOf(ps2).toCharArray()));
                            }
                        } catch (SQLException var17) {
                            throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var17));
                        }

                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                        }
                    } else {
                        ps.setNull(e + 1, 2005);
                        if(!this.isSlience()) {
                            this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                        }
                    }
                }
            } else if(ps2 != null) {
                try {
                    ps.setString(e + 1, ps2.toString());
                    if(!this.isSlience()) {
                        this.L.debug("\tSQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType());
                    }
                } catch (SQLException var30) {
                    throw new Exception("  SQLParameter:" + (e + 1) + "=" + ps2 + "," + es.getType() + ", error:" + Utils.getErrorStack(var30));
                }
            } else {
                ps.setNull(e + 1, 12);
                if(!this.isSlience()) {
                    this.L.debug("\tSQLParameter:" + (e + 1) + "=null," + es.getType());
                }
            }
        }

        ps.executeUpdate();
        ps.close();
        if(!var32.isEmpty()) {
            String var34 = "select * from " + this.tablename + " where id=\'" + var33 + "\'";
            StringBuilder var36 = new StringBuilder("update " + this.tablename + " set ");
            ArrayList var37 = new ArrayList();

            Map.Entry var39;
            for(Iterator rs = var32.entrySet().iterator(); rs.hasNext(); var36.append(((Column)var39.getKey()).getField() + " = ?")) {
                var39 = (Map.Entry)rs.next();
                var37.add((Column)var39.getKey());
                if(var37.size() != 1) {
                    var36.append(",");
                }
            }

            var36.append(" where id =\'" + var33 + "\'");
            PreparedStatement var40 = this.conn.prepareStatement(var34);
            ResultSet var41 = var40.executeQuery();
            HashMap values = new HashMap();
            if(var41.next()) {
                Iterator l = var37.iterator();

                while(l.hasNext()) {
                    Column i = (Column)l.next();
                    CLOB name = (CLOB)var41.getClob(i.getField());
                    Writer w = name.setCharacterStream(1L);
                    w.write((String)var32.get(i));
                    w.flush();
                    w.close();
                    values.put(i.getField(), name);
                }
            }

            var40.close();
            ps2 = null;
            var40 = this.conn.prepareStatement(var36.toString());
            int var42 = 0;

            for(int var43 = var37.size(); var42 < var43; ++var42) {
                Column var44 = (Column)var37.get(var42);
                var40.setClob(var42 + 1, (Clob)values.get(var44.getField()));
            }

            var40.executeUpdate();
            var40.close();
        }

        long var35 = System.currentTimeMillis();
        long var38 = var35 - s;
        if(!this.isSlience()) {
            this.L.info("Inserted Entity(" + this.tablename + ") into DB(" + s + ").............ok(" + var38 + "ms)");
        }

        this.condition = null;
        return var33;
    }

    public void delete() throws Exception {
        this.values.clear();
        long s = System.currentTimeMillis();
        this.L.debug("Start deleting record from table: " + this.tablename);
        int e = 0;

        for(int l = this.cols.size(); e < l; ++e) {
            Column es = (Column)this.cols.get(e);
            if(es.isKey()) {
                Object value = null;

                try {
                    value = this.getValue(es.getName());
                } catch (Exception var9) {
                    ;
                }

                if(value == null) {
                    throw new Exception("Entity[" + this.tablename + "] no primary key setting, can\'t do delete action");
                }

                this.L.debug("Check table record first...");
                String searchSql = "select count(*) num from " + this.tablename + " where " + es.getField() + " = \'" + value + "\'";
                String sql = "delete from " + this.tablename + " where " + es.getField() + " = \'" + value + "\'";
                if(es.getType() == ColumnType.INT || es.getType() == ColumnType.LONG || es.getType() == ColumnType.FLOAT) {
                    sql = "delete from " + this.tablename + " where " + es.getField() + " = " + value;
                    searchSql = "select count(*) num from " + this.tablename + " where " + es.getField() + " = " + value;
                }

                this.executeQuery(searchSql);
                if(this.getResultCount() == 1) {
                    if(this.getIntegerValue("num").intValue() > 0) {
                        this.executeUpdate(sql);
                    } else {
                        this.L.warn("No records need to delete in DB.............");
                    }
                } else {
                    this.L.warn("No records need to delete in DB.............");
                }
                break;
            }
        }

        this.clear();
        this.condition = null;
        long var10 = System.currentTimeMillis();
        long var11 = var10 - s;
        this.L.debug("Deleted records from table: " + this.tablename + "..........ok(" + var11 + "ms)");
    }

    public void update() throws Exception {
        this.values.clear();
        long s = System.currentTimeMillis();
        this.L.debug("Start update Entity(" + this.tablename + ") from DB..........");
        String id = null;
        String keyColumnName = null;
        int paramSize = 0;
        StringBuffer sql = new StringBuffer("update ");
        sql.append(this.tablename).append(" set ");
        boolean first = true;
        int oracleClob = 0;

        for(int ps = this.cols.size(); oracleClob < ps; ++oracleClob) {
            if(((Column)this.cols.get(oracleClob)).getValue() != null) {
                if(((Column)this.cols.get(oracleClob)).isKey()) {
                    id = ((Column)this.cols.get(oracleClob)).getValue().toString();
                    keyColumnName = ((Column)this.cols.get(oracleClob)).getField();
                } else {
                    if(paramSize != 0 || !first) {
                        sql.append(" , ");
                    }

                    ++paramSize;
                    sql.append(((Column)this.cols.get(oracleClob)).getField() + "=?");
                    first = false;
                }
            } else if(((Column)this.cols.get(oracleClob)).isNull()) {
                if(!first) {
                    sql.append(" , ");
                }

                if(paramSize == 0) {
                    sql.append(((Column)this.cols.get(oracleClob)).getField() + " = null ");
                } else {
                    sql.append(((Column)this.cols.get(oracleClob)).getField() + " = null");
                }

                first = false;
            }
        }

        HashMap var36 = new HashMap();
        sql.append(" where " + keyColumnName + " = ?");
        this.L.info("Update SQL:" + sql.toString());
        PreparedStatement var37 = this.conn.prepareStatement(sql.toString());
        int k = 0;
        int size = 0;

        for(int e = this.cols.size(); size < e; ++size) {
            Column updateSql = (Column)this.cols.get(size);
            Object es = updateSql.getValue();
            if(es != null && !updateSql.isKey()) {
                ++k;
                if(updateSql.getType() != ColumnType.STRING && updateSql.getType() != ColumnType.TEXT && updateSql.getType() != ColumnType.LONGTEXT && updateSql.getType() != ColumnType.MEDIUMTEXT) {
                    if(updateSql.getType() == ColumnType.DATE) {
                        if(es instanceof Date) {
                            try {
                                var37.setDate(k, (Date)es);
                            } catch (SQLException var34) {
                                throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var34));
                            }
                        } else {
                            if(!(es instanceof java.util.Date)) {
                                throw new Exception("Entity[" + this.tablename + "] update data failed for invalid Date value:" + es);
                            }

                            try {
                                var37.setDate(k, new Date(((java.util.Date)es).getTime()));
                            } catch (SQLException var33) {
                                throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var33));
                            }
                        }
                    } else if(updateSql.getType() == ColumnType.DATETIME) {
                        if(es instanceof Date) {
                            try {
                                var37.setTimestamp(k, new Timestamp(((Date)es).getTime()));
                            } catch (SQLException var32) {
                                throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var32));
                            }
                        } else if(es instanceof java.util.Date) {
                            try {
                                var37.setTimestamp(k, new Timestamp(((java.util.Date)es).getTime()));
                            } catch (SQLException var31) {
                                throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var31));
                            }
                        } else {
                            if(!(es instanceof Timestamp)) {
                                throw new Exception("Entity[" + this.tablename + "] update data failed for invalid Date value:" + es);
                            }

                            try {
                                var37.setTimestamp(k, (Timestamp)es);
                            } catch (SQLException var30) {
                                throw new Exception(var30);
                            }
                        }
                    } else if(updateSql.getType() == ColumnType.INT) {
                        if(es instanceof Integer) {
                            try {
                                var37.setInt(k, ((Integer)es).intValue());
                            } catch (SQLException var29) {
                                throw new Exception(var29);
                            }
                        } else {
                            try {
                                var37.setInt(k, Integer.parseInt(String.valueOf(es)));
                            } catch (Exception var28) {
                                throw new Exception("Entity[" + this.tablename + "] update data failed for invalid int value:" + es);
                            }
                        }
                    } else if(updateSql.getType() == ColumnType.LONG) {
                        if(es instanceof Long) {
                            try {
                                var37.setLong(k, ((Long)es).longValue());
                            } catch (SQLException var27) {
                                throw new Exception(var27);
                            }
                        } else {
                            try {
                                var37.setLong(k, Long.parseLong(String.valueOf(es)));
                            } catch (Exception var26) {
                                throw new Exception("Entity[" + this.tablename + "] update data failed for invalid long value:" + es);
                            }
                        }
                    } else if(updateSql.getType() == ColumnType.FLOAT) {
                        if(es instanceof Float) {
                            try {
                                var37.setFloat(k, ((Float)es).floatValue());
                            } catch (SQLException var25) {
                                throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var25));
                            }
                        } else {
                            try {
                                var37.setFloat(k, Float.parseFloat(String.valueOf(es)));
                            } catch (Exception var24) {
                                throw new Exception("Entity[" + this.tablename + "] update data failed for invalid float value:" + es);
                            }
                        }
                    } else if(updateSql.getType() == ColumnType.BLOB) {
                        try {
                            var37.setBlob(k, (Blob)es);
                        } catch (SQLException var23) {
                            throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var23));
                        }
                    } else if(updateSql.getType() == ColumnType.CLOB) {
                        try {
                            if(DBUtils.getRDBType() == DBType.Oracle) {
                                var36.put(updateSql, String.valueOf(es));
                                var37.setClob(k, CLOB.getEmptyCLOB());
                            } else {
                                var37.setClob(k, new SerialClob(String.valueOf(es).toCharArray()));
                            }
                        } catch (SQLException var22) {
                            throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var22));
                        }
                    }
                } else {
                    try {
                        var37.setString(k, es.toString());
                    } catch (SQLException var35) {
                        throw new Exception("  SQLParameter:" + k + "=" + es + "," + updateSql.getType() + ", error:" + Utils.getErrorStack(var35));
                    }
                }

                this.L.debug("\tSQLParameter:" + k + "=" + es + "," + updateSql.getType());
            }
        }

        var37.setString(paramSize + 1, id);
        size = var37.executeUpdate();
        this.condition = null;
        var37.close();
        if(!var36.isEmpty()) {
            String var38 = "select * from " + this.tablename + " where id=\'" + id + "\'";
            StringBuilder var40 = new StringBuilder("update " + this.tablename + " set ");
            ArrayList var41 = new ArrayList();

            Map.Entry ps2;
            for(Iterator rs = var36.entrySet().iterator(); rs.hasNext(); var40.append(((Column)ps2.getKey()).getField() + " = ?")) {
                ps2 = (Map.Entry)rs.next();
                var41.add((Column)ps2.getKey());
                if(var41.size() != 1) {
                    var40.append(",");
                }
            }

            var40.append(" where id =\'" + id + "\'");
            PreparedStatement var43 = this.conn.prepareStatement(var38);
            ResultSet var44 = var43.executeQuery();
            HashMap values = new HashMap();
            if(var44.next()) {
                Iterator l = var41.iterator();

                while(l.hasNext()) {
                    Column i = (Column)l.next();
                    CLOB name = (CLOB)var44.getClob(i.getField());
                    Writer w = name.setCharacterStream(1L);
                    w.write((String)var36.get(i));
                    w.flush();
                    w.close();
                    values.put(i.getField(), name);
                }
            }

            var43.close();
            ps2 = null;
            var43 = this.conn.prepareStatement(var40.toString());
            int var45 = 0;

            for(int var46 = var41.size(); var45 < var46; ++var45) {
                Column var47 = (Column)var41.get(var45);
                var43.setClob(var45 + 1, (Clob)values.get(var47.getField()));
            }

            var43.executeUpdate();
            var43.close();
        }

        if(size <= 0) {
            this.L.info("No records Updated for Entity(" + this.tablename + ")........");
        } else {
            long var39 = System.currentTimeMillis();
            long var42 = var39 - s;
            this.L.info("Updated: " + size + " records, Entity(" + this.tablename + ") into DB.............ok(" + var42 + "ms)");
        }

    }

    public void updateAll() throws Exception {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column col = (Column)this.cols.get(i);
            Object val = this.getValue(col.getName());
            if(val == null || val.toString().length() <= 0) {
                this.setNullValue(col.getName());
            }
        }

        this.update();
    }

    public int search() throws Exception {
        return this.search(0, 0);
    }

    public int search(int start, int end) throws Exception {
        this.values.clear();
        this.maxResultCount = 0;
        String sql = this.createSearchSql();
        String maxSql = "select count(*) num from (" + this.createSearchSqlWithValue() + ") TEMPTABLE";

        try {
            this.executeQuery(maxSql, false);
            Object size = ((Map)this.values.get(0)).get("num");
            this.maxResultCount = Integer.parseInt(size.toString());
        } catch (Exception var10) {
            throw new Exception("Entity [" + this.tablename + "] search error: Get total result count failed:" + var10);
        }

        if(start != 0 && end != 0) {
            sql = "select * from (select rownum  rowsn, temp_table.* from (" + sql + ") temp_table) where rowsn between " + start + " and " + end;
        }

        int var11 = this.doSearch(sql);
        if(this.values.size() > 0) {
            Map map = (Map)this.values.get(0);
            if(map != null) {
                for(int i = 0; i < this.cols.size(); ++i) {
                    Column col = (Column)this.cols.get(i);
                    Object value = map.get(col.getName());
                    if(value != null) {
                        col.setValue(value);
                    } else {
                        col.setValue((Object)null);
                    }
                }
            }
        }

        return var11;
    }

    public void setValue(String param, Object value) throws Exception {
        if(this.cols != null && this.cols.size() > 0) {
            Column column = null;
            int i = 0;

            for(int l = this.cols.size(); i < l; ++i) {
                Column col = (Column)this.cols.get(i);
                if(col.getName() != null && col.getName().equalsIgnoreCase(param)) {
                    column = col;
                    break;
                }
            }

            if(column != null) {
                column.setValue(value);
            } else {
                this.L.warn("Entity[" + this.tablename + "] set value failed: invalid column name[" + param + "]...");
            }

        } else {
            throw new Exception("Entity[" + this.tablename + "] set value failed:No \'column\' seting");
        }
    }

    public void setNullValue(String param) throws Exception {
        if(this.cols != null && this.cols.size() > 0) {
            Column column = null;
            int i = 0;

            for(int l = this.cols.size(); i < l; ++i) {
                Column col = (Column)this.cols.get(i);
                if(col.getName() != null && col.getName().equalsIgnoreCase(param)) {
                    column = col;
                    break;
                }
            }

            if(column != null) {
                column.setNullValue();
            } else {
                throw new Exception("Entity[" + this.tablename + "] set value failed: invalid column name[" + param + "]...");
            }
        } else {
            throw new Exception("Entity[" + this.tablename + "] set value failed:No \'column\' seting");
        }
    }

    public void setValue(String param, Object value, int i) throws Exception {
        if(this.values.size() > i && this.values.get(i) != null) {
            ((Map)this.values.get(i)).put(param, value);
        } else {
            HashMap map = new HashMap();
            map.put(param, value);
            this.values.add(map);
        }

    }

    public Object getValue(String param) throws Exception {
        if(this.cols != null && this.cols.size() > 0) {
            int i = 0;

            for(int l = this.cols.size(); i < l; ++i) {
                Column col = (Column)this.cols.get(i);
                if(col.getName() != null && col.getName().equalsIgnoreCase(param)) {
                    return col.getValue();
                }
            }

            return null;
        } else {
            throw new Exception("Entity[" + this.tablename + "] get value failed:No \'column\' seting");
        }
    }

    private Column getColumn(String name) {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column col = (Column)this.cols.get(i);
            if(col.getName() != null && col.getName().equalsIgnoreCase(name)) {
                return col;
            }
        }

        return null;
    }

    private void validate() throws Exception {
        if(this.slice) {
            if(this.tablename == null) {
                throw new Exception("插入数据没有定义表名");
            }

            if(this.tablename.equalsIgnoreCase(this.tablenameBak)) {
                throw new Exception("分片表不能直接存储数据到原始定义表");
            }
        }

        Iterator var2 = this.cols.iterator();

        while(var2.hasNext()) {
            Column col = (Column)var2.next();
            if(col != null) {
                Object value = col.getValue();
                if(!col.isNullable() && !col.isAutoGenerate() && (value == null || value.toString().length() <= 0)) {
                    this.L.warn("Entity [" + this.tablename + "] validate failed : field " + col.getName() + " does not allow nulls !");
                }
            }
        }

    }

    private String createSearchSqlWithValue() throws Exception, Exception {
        try {
            StringBuffer e = new StringBuffer("select ");
            int k = 0;

            int i;
            for(i = this.cols.size(); k < i; ++k) {
                Column l = (Column)this.cols.get(k);
                if(k != 0) {
                    e.append(",");
                }

                e.append(l.getField() + " as " + l.getName());
            }

            e.append(" from " + this.tablename + " where ");
            k = 0;
            i = 0;

            for(int var9 = this.cols.size(); i < var9; ++i) {
                Column col = (Column)this.cols.get(i);
                if(col.getValue() != null && col.getValue().toString().length() > 0) {
                    if(k != 0) {
                        e.append(" and ");
                    }

                    e.append(col.getField() + " = ");
                    if(col.getType() != ColumnType.FLOAT && col.getType() != ColumnType.INT && col.getType() != ColumnType.LONG) {
                        Date date;
                        SimpleDateFormat sf;
                        if(col.getType() == ColumnType.DATETIME) {
                            date = this.getDateValue(col.getName());
                            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            e.append("to_timestamp(\'" + sf.format(date) + "\',\'yyyy-mm-dd hh24:mi:ss\')");
                        } else if(col.getType() == ColumnType.DATE) {
                            date = this.getDateValue(col.getName());
                            sf = new SimpleDateFormat("yyyy-MM-dd");
                            e.append("to_date(\'" + sf.format(date) + "\',\'yyyy-mm-dd\')");
                        } else {
                            e.append("\'" + col.getValue() + "\'");
                        }
                    } else {
                        e.append(col.getValue());
                    }

                    ++k;
                } else if(col.isNull()) {
                    if(k != 0) {
                        e.append(" and ");
                    }

                    e.append(col.getField() + " is null");
                    ++k;
                }
            }

            return e.toString();
        } catch (Exception var8) {
            throw var8;
        }
    }

    private String createSearchSql() throws Exception {
        StringBuffer sb = new StringBuffer("select ");
        int k = 0;

        int i;
        for(i = this.cols.size(); k < i; ++k) {
            Column l = (Column)this.cols.get(k);
            if(k != 0) {
                sb.append(",");
            }

            sb.append(l.getField() + " as " + l.getName());
        }

        sb.append(" from " + this.tablename + " where ");
        k = 0;
        i = 0;

        for(int var6 = this.cols.size(); i < var6; ++i) {
            Column col = (Column)this.cols.get(i);
            if(col.getValue() != null && col.getValue().toString().length() > 0) {
                if(k != 0) {
                    sb.append(" and ");
                }

                sb.append(col.getField() + " = ?");
                ++k;
            } else if(col.isNull()) {
                if(k != 0) {
                    sb.append(" and ");
                }

                sb.append(col.getField() + " is null");
                ++k;
            }
        }

        if(this.condition != null && this.condition.trim().length() > 0) {
            sb.append(" " + this.condition);
            this.condition = null;
        }

        return sb.toString();
    }

    public List<Column> getCols() {
        return this.cols;
    }

    public String getStringValue(String param) throws Exception {
        return this.getStringValue(param, 0);
    }

    public Integer getIntegerValue(String param) throws Exception {
        return this.getIntegerValue(param, 0);
    }

    public Float getFloatValue(String param) throws Exception {
        return this.getFloatValue(param, 0);
    }

    public Double getDoubleValue(String param) throws Exception {
        return this.getDoubleValue(param, 0);
    }

    public Date getDateValue(String param) throws Exception {
        return this.getDateValue(param, 0);
    }

    public String getStringValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = null;
        if(this.values.size() > i) {
            map = (Map)this.values.get(i);
        } else if(i == 0) {
            List obj = this.getCols();
            int k = 0;

            for(int l = obj.size(); k < l; ++k) {
                Column c = (Column)obj.get(k);
                if(c.getName().equalsIgnoreCase(param)) {
                    Object val = c.getValue();
                    if(val != null) {
                        return String.valueOf(val);
                    }

                    return "";
                }
            }
        }

        if(map != null) {
            Object var9 = map.get(param);
            return var9 != null?(var9 instanceof Clob?clob2String((Clob)var9):String.valueOf(var9)):"";
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    public static final String clob2String(Clob clob) throws Exception {
        if(clob == null) {
            return "";
        } else {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(clob.getCharacterStream());
                StringBuffer e = new StringBuffer();
                String line = null;

                while((line = reader.readLine()) != null) {
                    e.append(line);
                }

                String var5 = e.toString();
                return var5;
            } catch (Exception var12) {
                throw var12;
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var11) {
                        ;
                    }
                }

            }
        }
    }

    public Integer getIntegerValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map != null) {
            Double temp = Double.valueOf(0.0D);
            Object obj = map.get(param);
            if(obj != null) {
                try {
                    temp = Double.valueOf(Double.parseDouble(String.valueOf(obj)));
                } catch (Exception var7) {
                    throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to integer");
                }
            }

            return Integer.valueOf(temp.intValue());
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    public Float getFloatValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map == null) {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        } else {
            Float temp = Float.valueOf(0.0F);
            Object obj = map.get(param);
            String objStr = String.valueOf(obj);
            if(obj != null) {
                try {
                    temp = Float.valueOf(Float.parseFloat(objStr));
                } catch (Exception var8) {
                    if(!objStr.matches("\\d*,?\\d*\\.?\\d*")) {
                        throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to float");
                    }

                    objStr = objStr.replace(",", "");
                    temp = Float.valueOf(Float.parseFloat(objStr));
                }
            }

            return temp;
        }
    }

    public Double getDoubleValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map != null) {
            Double temp = Double.valueOf(0.0D);
            Object obj = map.get(param);
            if(obj != null) {
                try {
                    temp = Double.valueOf(Double.parseDouble(String.valueOf(obj)));
                } catch (Exception var7) {
                    throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to double");
                }
            }

            return temp;
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    public boolean getBooleanValue(String param) throws Exception {
        return this.getBooleanValue(param, 0);
    }

    public boolean getBooleanValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map != null) {
            Object obj = map.get(param);
            if(obj != null) {
                try {
                    return Utils.isTrue(obj);
                } catch (Exception var6) {
                    throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to Boolean");
                }
            } else {
                return false;
            }
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    public Date getDateValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map != null) {
            new Date(System.currentTimeMillis());
            Object obj = map.get(param);
            if(obj != null) {
                Date temp;
                if(obj instanceof Date) {
                    temp = (Date)obj;
                } else if(obj instanceof java.util.Date) {
                    temp = new Date(((java.util.Date)obj).getTime());
                } else if(obj instanceof Timestamp) {
                    temp = new Date(((Timestamp)obj).getTime());
                } else if(obj instanceof DATE) {
                    temp = new Date(((DATE)obj).dateValue().getTime());
                } else if(obj instanceof TIMESTAMP) {
                    temp = new Date(((TIMESTAMP)obj).dateValue().getTime());
                } else {
                    try {
                        SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            temp = (Date)e.parse(String.valueOf(obj));
                        } catch (Exception var10) {
                            e = new SimpleDateFormat("yyyyMMdd");

                            try {
                                temp = (Date)e.parse(String.valueOf(obj));
                            } catch (Exception var9) {
                                throw var9;
                            }
                        }
                    } catch (Exception var11) {
                        throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to date");
                    }
                }

                return temp;
            } else {
                return null;
            }
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    private int doSearch(String sql) throws Exception {
        this.values.clear();
        long s = System.currentTimeMillis();
        PreparedStatement ps = this.conn.prepareStatement(sql);
        this.L.info("Execute SQL:" + sql);
        int k = 0;
        int rs = 0;

        for(int e = this.cols.size(); rs < e; ++rs) {
            Column col = (Column)this.cols.get(rs);
            if(col.getValue() != null && col.getValue().toString().length() > 0) {
                ++k;
                if(col.getType() != ColumnType.STRING && col.getType() != ColumnType.TEXT && col.getType() != ColumnType.LONGTEXT && col.getType() != ColumnType.MEDIUMTEXT) {
                    if(col.getType() == ColumnType.INT) {
                        ps.setInt(k, ((Integer)col.getValue()).intValue());
                    } else if(col.getType() == ColumnType.LONG) {
                        ps.setLong(k, ((Long)col.getValue()).longValue());
                    } else if(col.getType() == ColumnType.FLOAT) {
                        ps.setFloat(k, ((Float)col.getValue()).floatValue());
                    } else if(col.getType() == ColumnType.DATE) {
                        ps.setDate(k, (Date)col.getValue());
                    } else if(col.getType() == ColumnType.DATETIME) {
                        ps.setTimestamp(k, (Timestamp)col.getValue());
                    }
                } else {
                    ps.setString(k, col.getValue().toString());
                }

                this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + col.getValue() + "," + col.getType());
            }
        }

        ResultSet var11 = ps.executeQuery();
        this.values = DBUtils.getResultSet(var11);
        var11.close();
        ps.close();
        long var12 = System.currentTimeMillis();
        long es = var12 - s;
        this.L.info("Executed.............ok(" + es + "ms)");
        return this.values.size();
    }

    public int executeUpdate(String sql) throws Exception {
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            long s = System.currentTimeMillis();
            this.values.clear();
            PreparedStatement ps = this.conn.prepareStatement(sql);
            this.L.info("Execute SQL(" + s + "):" + sql);
            int temp = ps.executeUpdate();
            this.clear();
            ps.close();
            long e = System.currentTimeMillis();
            long es = e - s;
            this.L.info("Executed(" + s + "):" + temp + " records,.............ok(" + es + "ms)");
            this.condition = null;
            return temp;
        }
    }

    public int getResultCount() throws Exception {
        return this.values.size();
    }

    public int getMaxResultCount() throws Exception {
        if(this.maxResultCount <= 0) {
            this.maxResultCount = this.getResultCount();
        }

        return this.maxResultCount;
    }

    private int executeQueryWithOutMaxResult(String sql, int start, int end) throws Exception {
        if(sql != null && sql.length() > 0) {
            long s = System.currentTimeMillis();
            if(start != 0 || end != 0) {
                DBType ps = DBUtils.getRDBType();
                if(ps == DBType.Oracle) {
                    sql = "select * from (select rownum  rowsn, temp_table.* from (" + sql + ") temp_table) xxx where rowsn between " + start + " and " + end;
                } else if(ps == DBType.Mysql) {
                    sql = sql + " limit " + (start - 1) + "," + (end - start + 1);
                }
            }

            PreparedStatement ps1 = this.conn.prepareStatement(sql);
            this.L.info("Execute SQL(" + s + "):" + sql);
            ResultSet rs = ps1.executeQuery();
            long e = System.currentTimeMillis();
            long es = e - s;
            this.values = DBUtils.getResultSet(rs);
            this.L.info("Executed(" + s + "):" + this.values.size() + " records,.............ok(" + es + "ms)");
            ps1.close();
            rs.close();
            this.condition = null;
            return this.values.size();
        } else {
            throw new Exception("SQL is null");
        }
    }

    public int executeQuery(String sql, int start, int end) throws Exception {
        return this.executeQueryWithOutMaxResult(sql, start, end);
    }

    private int executeQuery(String sql, boolean clear) throws Exception {
        long s = System.currentTimeMillis();
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            this.values.clear();
            PreparedStatement ps = this.conn.prepareStatement(sql);
            this.L.info("Execute SQL(" + s + "):" + sql);
            ResultSet rs = ps.executeQuery();
            if(clear) {
                this.clear();
            }

            long e = System.currentTimeMillis();
            long es = e - s;
            this.values = DBUtils.getResultSet(rs);
            this.L.info("Executed(" + s + "):" + this.values.size() + " records,.............ok(" + es + "ms)");
            this.maxResultCount = this.values.size();
            rs.close();
            ps.close();
            this.condition = null;
            return this.values.size();
        }
    }

    public int executeQuery(String sql) throws Exception {
        this.condition = null;
        return this.executeQuery(sql, true);
    }

    public String getFormatStringValue(String param, String format) throws Exception {
        return this.getFormatStringValue(param, format, 0);
    }

    public String getFormatStringValue(String param, String format, int i) throws Exception {
        try {
            Date e = this.getDateValue(param, i);
            SimpleDateFormat sf = new SimpleDateFormat(format);
            return sf.format(e);
        } catch (Exception var6) {
            return "";
        }
    }

    public int executeQueryWithMaxResult(String sql, int start, int end) throws Exception {
        if(sql != null && sql.length() > 0) {
            long s = System.currentTimeMillis();
            PreparedStatement ps = this.conn.prepareStatement("select count(*) num from (" + sql + ") xxx");
            this.L.info("Execute SQL(" + s + "):" + "select count(*) num from (" + sql + ") xxx");
            ResultSet rs = ps.executeQuery();
            long e = System.currentTimeMillis();
            long es = e - s;
            this.L.info("Executed(" + s + ").............ok(" + es + "ms)");
            if(rs.next()) {
                try {
                    String type = rs.getString("num");
                    this.maxResultCount = Integer.parseInt(type);
                } catch (Exception var13) {
                    this.maxResultCount = 0;
                }
            }

            if(start != 0 || end != 0) {
                DBType type1 = DBUtils.getRDBType();
                if(type1 == DBType.Oracle) {
                    sql = "select * from (select rownum  rowsn, temp_table.* from (" + sql + ") temp_table) xxx where rowsn between " + start + " and " + end;
                } else if(type1 == DBType.Mysql) {
                    sql = sql + " limit " + (start - 1) + "," + (end - start + 1);
                }
            }

            s = System.currentTimeMillis();
            ps = this.conn.prepareStatement(sql);
            this.L.info("Execute SQL(" + s + "):" + sql);
            rs = ps.executeQuery();
            e = System.currentTimeMillis();
            es = e - s;
            this.values = DBUtils.getResultSet(rs);
            this.L.info("Executed(" + s + "):" + this.values.size() + " records,.............ok(" + es + "ms)");
            ps.close();
            rs.close();
            this.condition = null;
            return this.values.size();
        } else {
            throw new Exception("SQL is null");
        }
    }

    public int executeQueryWithMaxResult(String sql, Object[] params, int start, int end) throws Exception {
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            long s = System.currentTimeMillis();
            String sql2 = sql;
            if(start != 0 || end != 0) {
                DBType countSql = DBUtils.getRDBType();
                if(countSql == DBType.Oracle) {
                    sql2 = "select * from (select rownum  rowsn, temp_table.* from (" + sql + ") temp_table) xxx where rowsn between " + start + " and " + end;
                } else if(countSql == DBType.Mysql) {
                    sql2 = sql + " limit " + (start - 1) + "," + (end - start + 1);
                }
            }

            String var20 = "select count(*) num from (" + sql + ") xxx";
            this.condition = null;
            this.L.info("Execute SQL(" + s + "):" + var20);
            PreparedStatement countPs = this.conn.prepareStatement(var20);
            int countrs = 0;

            int e;
            Object obj;
            for(int sx = params.length; countrs < sx; ++countrs) {
                Object ps = params[countrs];
                if(ps instanceof Object[]) {
                    Object[] rs = (Object[])ps;
                    if(rs.length != 2) {
                        throw new Exception("Invalid DB Parmameter length,you must provide parameter like this:new Object[]{data,Types.INT}");
                    }

                    e = Integer.parseInt(rs[1].toString());
                    obj = rs[0];
                    if(obj != null) {
                        if(e == 91) {
                            countPs.setDate(countrs + 1, (Date)obj);
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:DATE");
                        } else if(e == 93) {
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:TIMESTAMP");
                        } else if(e == 4) {
                            countPs.setInt(countrs + 1, ((Integer)obj).intValue());
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:INTEGER");
                        } else if(e == 6) {
                            countPs.setFloat(countrs + 1, ((Float)obj).floatValue());
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:FLOAT");
                        } else if(e == 8) {
                            countPs.setDouble(countrs + 1, ((Double)obj).doubleValue());
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:DOUBLE");
                        } else if(e == 2004) {
                            countPs.setBlob(countrs + 1, (Blob)obj);
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:BLOB");
                        } else {
                            countPs.setObject(countrs + 1, obj);
                            this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + obj + ",Types:String");
                        }
                    } else {
                        countPs.setNull(countrs + 1, e);
                        this.L.debug("\tSQLParameter:" + (countrs + 1) + "=NULL,Types:" + e);
                    }
                } else {
                    if(ps == null) {
                        countPs.setNull(countrs + 1, 12);
                    } else {
                        countPs.setObject(countrs + 1, ps);
                    }

                    this.L.debug("\tSQLParameter:" + (countrs + 1) + "=" + params[countrs]);
                }
            }

            ResultSet var21 = countPs.executeQuery();
            List var22 = DBUtils.getResultSet(var21);

            try {
                String var23 = String.valueOf(((Map)var22.get(0)).get("num"));
                this.maxResultCount = Integer.parseInt(var23);
            } catch (Exception var19) {
                this.maxResultCount = 0;
            }

            this.L.info("Executed(" + s + "):" + this.maxResultCount + " maxResultCount,.............ok");
            if(this.maxResultCount <= 0) {
                return 0;
            } else {
                s = System.currentTimeMillis();
                this.L.info("Execute SQL(" + s + "):" + sql2);
                PreparedStatement var24 = this.conn.prepareStatement(sql2);
                int var25 = 0;

                for(e = params.length; var25 < e; ++var25) {
                    obj = params[var25];
                    if(obj instanceof Object[]) {
                        Object[] es = (Object[])obj;
                        if(es.length != 2) {
                            throw new Exception("Invalid DB Parmameter length,you must provide parameter like this:new Object[]{data,Types.INT}");
                        }

                        int type = Integer.parseInt(es[1].toString());
                        Object data = es[0];
                        if(data != null) {
                            if(type == 91) {
                                var24.setDate(var25 + 1, (Date)data);
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:DATE");
                            } else if(type == 93) {
                                var24.setTimestamp(var25 + 1, (Timestamp)data);
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:TIMESTAMP");
                            } else if(type == 4) {
                                var24.setInt(var25 + 1, ((Integer)data).intValue());
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:INTEGER");
                            } else if(type == 6) {
                                var24.setFloat(var25 + 1, ((Float)data).floatValue());
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:FLOAT");
                            } else if(type == 8) {
                                var24.setDouble(var25 + 1, ((Double)data).doubleValue());
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:DOUBLE");
                            } else if(type == 2004) {
                                var24.setBlob(var25 + 1, (Blob)data);
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:BLOB");
                            } else {
                                var24.setObject(var25 + 1, data);
                                this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + data + ",Types:String");
                            }
                        } else {
                            var24.setNull(var25 + 1, type);
                            this.L.debug("\tSQLParameter:" + (var25 + 1) + "=NULL,Types:" + type);
                        }
                    } else {
                        if(obj == null) {
                            var24.setNull(var25 + 1, 12);
                        } else {
                            var24.setObject(var25 + 1, obj);
                        }

                        this.L.debug("\tSQLParameter:" + (var25 + 1) + "=" + params[var25]);
                    }
                }

                ResultSet var26 = var24.executeQuery();
                this.values = DBUtils.getResultSet(var26);
                long var27 = System.currentTimeMillis();
                long var28 = var27 - s;
                this.L.info("Executed(" + s + "):" + this.values.size() + " records,.............ok(" + var28 + "ms)");
                countPs.close();
                var21.close();
                var24.close();
                var26.close();
                return this.values.size();
            }
        }
    }

    public int executeQuery(String sql, Object[] params, int start, int end) throws Exception {
        return this.executeQueryWithOutMaxResult(sql, params, start, end);
    }

    private int executeQueryWithOutMaxResult(String sql, Object[] params, int start, int end) throws Exception {
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            String sql2 = sql;
            if(start != 0 || end != 0) {
                DBType s = DBUtils.getRDBType();
                if(s == DBType.Oracle) {
                    sql2 = "select * from (select rownum  rowsn, temp_table.* from (" + sql + ") temp_table) xxx where rowsn between " + start + " and " + end;
                } else if(s == DBType.Mysql) {
                    sql2 = sql + " limit " + (start - 1) + "," + (end - start + 1);
                }
            }

            long var15 = System.currentTimeMillis();
            this.L.info("Execute SQL(" + var15 + "):" + sql2);
            PreparedStatement ps = this.conn.prepareStatement(sql2);
            int rs = 0;

            for(int e = params.length; rs < e; ++rs) {
                Object obj = params[rs];
                if(obj instanceof Object[]) {
                    Object[] es = (Object[])obj;
                    if(es.length != 2) {
                        throw new Exception("Invalid DB Parmameter length,you must provide parameter like this:new Object[]{data,Types.INT}");
                    }

                    int type = Integer.parseInt(es[1].toString());
                    Object data = es[0];
                    if(data != null) {
                        if(type == 91) {
                            ps.setDate(rs + 1, (Date)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:DATE");
                        } else if(type == 93) {
                            ps.setTimestamp(rs + 1, (Timestamp)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:TIMESTAMP");
                        } else if(type == 4) {
                            ps.setInt(rs + 1, ((Integer)data).intValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:INTEGER");
                        } else if(type == 6) {
                            ps.setFloat(rs + 1, ((Float)data).floatValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:FLOAT");
                        } else if(type == 8) {
                            ps.setDouble(rs + 1, ((Double)data).doubleValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:DOUBLE");
                        } else if(type == 2004) {
                            ps.setBlob(rs + 1, (Blob)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:BLOB");
                        } else {
                            ps.setObject(rs + 1, data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:String");
                        }
                    } else {
                        ps.setNull(rs + 1, type);
                        this.L.debug("\tSQLParameter:" + (rs + 1) + "=NULL,Types:" + type);
                    }
                } else {
                    if(obj == null) {
                        ps.setNull(rs + 1, 12);
                    } else {
                        ps.setObject(rs + 1, obj);
                    }

                    this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + params[rs]);
                }
            }

            ResultSet var16 = ps.executeQuery();
            this.values = DBUtils.getResultSet(var16);
            long var17 = System.currentTimeMillis();
            long var18 = var17 - var15;
            this.L.info("Executed(" + var15 + "):" + this.values.size() + " records,.............ok(" + var18 + "ms)");
            ps.close();
            var16.close();
            return this.values.size();
        }
    }

    public void addOtherCondition(String condition) {
        this.condition = condition;
    }

    public Long getLongValue(String param) throws Exception {
        return this.getLongValue(param, 0);
    }

    public BigDecimal getBigDecimalValue(String param) throws Exception {
        return this.getBigDecimalValue(param, 0);
    }

    public BigDecimal getBigDecimalValue(String param, int i) throws Exception {
        try {
            return new BigDecimal(this.getStringValue(param, i));
        } catch (Exception var4) {
            this.L.error(var4);
            return new BigDecimal(0);
        }
    }

    public Long getLongValue(String param, int i) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Map map = (Map)this.values.get(i);
        if(map != null) {
            Double temp = Double.valueOf(0.0D);
            Object obj = map.get(param);
            if(obj != null) {
                try {
                    temp = Double.valueOf(Double.parseDouble(String.valueOf(obj)));
                } catch (Exception var7) {
                    throw new Exception("Get entity[" + this.tablename + "] result value error: can\'t convert result value [" + obj + "]to long");
                }
            }

            return Long.valueOf(temp.longValue());
        } else {
            throw new Exception("Get entity[" + this.tablename + "] result value error:invalid result row num:" + i + ",entity total result row num is :" + this.values.size());
        }
    }

    public int executeUpdate(String sql, Object[] params) throws Exception {
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            long s = System.currentTimeMillis();
            if(params != null && params.length > 0) {
                this.values.clear();
                PreparedStatement ps = this.conn.prepareStatement(sql);
                this.L.info("Execute SQL(" + s + "):" + sql);
                int temp = 0;

                for(int e = params.length; temp < e; ++temp) {
                    Object obj = params[temp];
                    if(obj instanceof Object[]) {
                        Object[] es = (Object[])obj;
                        if(es.length != 2) {
                            throw new Exception("Invalid DB Parmameter length,you must provide parameter like this:new Object[]{data,Types.INT}");
                        }

                        int type = Integer.parseInt(es[1].toString());
                        Object data = es[0];
                        if(data != null) {
                            if(type == 91) {
                                ps.setDate(temp + 1, (Date)data);
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:DATE");
                            } else if(type == 93) {
                                ps.setTimestamp(temp + 1, (Timestamp)data);
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:TIMESTAMP");
                            } else if(type == 4) {
                                ps.setInt(temp + 1, ((Integer)data).intValue());
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:INTEGER");
                            } else if(type == 6) {
                                ps.setFloat(temp + 1, ((Float)data).floatValue());
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:FLOAT");
                            } else if(type == 8) {
                                ps.setDouble(temp + 1, ((Double)data).doubleValue());
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:DOUBLE");
                            } else if(type == 2004) {
                                ps.setBlob(temp + 1, (Blob)data);
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:BLOB");
                            } else {
                                ps.setObject(temp + 1, data);
                                this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + data + ",Types:String");
                            }
                        } else {
                            ps.setNull(temp + 1, type);
                            this.L.debug("\tSQLParameter:" + (temp + 1) + "=NULL,Types:" + type);
                        }
                    } else {
                        if(obj == null) {
                            ps.setNull(temp + 1, 12);
                        } else {
                            ps.setObject(temp + 1, obj);
                        }

                        this.L.debug("\tSQLParameter:" + (temp + 1) + "=" + params[temp]);
                    }
                }

                temp = ps.executeUpdate();
                this.clear();
                ps.close();
                long var12 = System.currentTimeMillis();
                long var13 = var12 - s;
                this.L.info("Executed(" + s + "):" + temp + " records,.............ok(" + var13 + "ms)");
                this.condition = null;
                return temp;
            } else {
                return this.executeUpdate(sql);
            }
        }
    }

    public List<Map<String, Object>> getValues() {
        if(this.values == null) {
            this.values = new ArrayList();
        }

        return this.values;
    }

    public JSONObject toJson(List<Map<String, Object>> list) throws JSONException {
        JSONObject o = new JSONObject();
        if(list == null || list.size() <= 0) {
            o.put("listData", new JSONArray());
            o.put("size", 0);
        }

        JSONArray temp = new JSONArray();
        int i = 0;

        for(int l = list.size(); i < l; ++i) {
            Map m = (Map)list.get(i);
            JSONObject oo = new JSONObject();
            Iterator var9 = m.entrySet().iterator();

            while(var9.hasNext()) {
                Map.Entry tr = (Map.Entry)var9.next();
                Column col = this.getColumn((String)tr.getKey());
                if(col != null) {
                    oo.put((String)tr.getKey(), col.getValue(tr.getValue()));
                } else {
                    oo.put((String)tr.getKey(), "");
                }
            }

            temp.put(oo);
        }

        o.put("listData", temp);
        o.put("size", list.size());
        return o;
    }

    public JSONObject toJson(String tablename) throws Exception {
        Object li = this.getValues();
        if(this.cols.size() <= 0) {
            if(tablename == null) {
                throw new Exception("请设置数据表名");
            }

            ArrayList o = new ArrayList();
            o.addAll((Collection)li);
            this.initialize(tablename, (Connection)this.conn);
            li = o;
        }

        if(li == null || ((List)li).size() <= 0) {
            li = new ArrayList();
        }

        JSONObject o1 = this.toJson((List)li);

        try {
            o1.put("total", this.getMaxResultCount() <= 0?((List)li).size():this.getMaxResultCount());
        } catch (Exception var5) {
            o1.put("total", 0);
        }

        return o1;
    }

    public JSONObject toJson() throws Exception {
        return this.toJson((String)this.tablenameBak);
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getTableComment() {
        return this.tableComment;
    }

    public String toJsonString() throws Exception {
        return this.toJson().toString();
    }

    public void DDL(boolean exists) throws Exception {
        HashMap tableInfos = new HashMap();
        HashMap dataInfos = new HashMap();
        ArrayList list = new ArrayList();
        dataInfos.put(this.tablename + "tablecode", this.tablename);
        dataInfos.put(this.tablename + "tablename", this.getTableComment());
        Iterator var6 = this.cols.iterator();

        while(var6.hasNext()) {
            Column c = (Column)var6.next();
            list.add(c.getField());
            String colid = c.getField();
            dataInfos.put(colid + "colname", c.getField());
            dataInfos.put(colid + "colcode", c.getField());
            dataInfos.put(colid + "datatype", "" + c.getType());
            dataInfos.put(colid + "length", String.valueOf(c.getLength()));
            dataInfos.put(colid + "mandatory", c.isNullable()?"N":"Y");
            dataInfos.put(colid + "comment", c.getComment());
            if(c.isKey()) {
                dataInfos.put(colid + "primarykey", "Y");
                dataInfos.put(colid + "autoC", c.isAutoGenerate()?"Y":"N");
            }
        }

        tableInfos.put(this.tablename, list);
        if(!exists) {
            DBUtils.createTable2System(tableInfos, dataInfos, this.conn);
            DBUtils.createDBTable(this.tablename, this.conn);
        }

    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public void copyTableFrom(String fromTable, String toTable) throws Exception {
        if(fromTable != null && fromTable.length() > 0 && toTable != null && toTable.length() > 0) {
            File file = MsInfo.getFile(new File(NettyUtils.getRootContent() + "/configures/database/rdb"), fromTable, "xml");
            if(file == null) {
                throw new Exception("找不到复制的数据表的配置文件");
            } else {
                String path = file.getParentFile().getAbsolutePath() + "/" + toTable + ".xml";
                File destFile = new File(path);
                String sql = "CREATE TABLE " + toTable + " LIKE " + fromTable;
                PreparedStatement ps = this.conn.prepareStatement(sql);
                this.L.info("Copy tabele:" + fromTable + " to :" + toTable);
                ps.executeUpdate();
                FileUtils.copyFile(file, destFile);
            }
        } else {
            throw new Exception("复制的数据表或者复制到的数据表名为空");
        }
    }

    public boolean isSlience() {
        return this.slience;
    }

    public void setSlience(boolean slience) {
        this.slience = slience;
    }

    public int executeQuery(String sql, Object[] params) throws Exception {
        if(sql == null) {
            throw new Exception("SQL is null");
        } else {
            long s = System.currentTimeMillis();
            this.condition = null;
            this.L.info("Execute SQL(" + s + "):" + sql);
            PreparedStatement ps = this.conn.prepareStatement(sql);
            int rs = 0;

            for(int e = params.length; rs < e; ++rs) {
                Object obj = params[rs];
                if(obj instanceof Object[]) {
                    Object[] es = (Object[])obj;
                    if(es.length != 2) {
                        throw new Exception("Invalid DB Parmameter length,you must provide parameter like this:new Object[]{data,Types.INT}");
                    }

                    int type = Integer.parseInt(es[1].toString());
                    Object data = es[0];
                    if(data != null) {
                        if(type == 91) {
                            ps.setDate(rs + 1, (Date)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:DATE");
                        } else if(type == 93) {
                            ps.setTimestamp(rs + 1, (Timestamp)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:TIMESTAMP");
                        } else if(type == 4) {
                            ps.setInt(rs + 1, ((Integer)data).intValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:INTEGER");
                        } else if(type == 6) {
                            ps.setFloat(rs + 1, ((Float)data).floatValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:FLOAT");
                        } else if(type == 8) {
                            ps.setDouble(rs + 1, ((Double)data).doubleValue());
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:DOUBLE");
                        } else if(type == 2004) {
                            ps.setBlob(rs + 1, (Blob)data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:BLOB");
                        } else {
                            ps.setObject(rs + 1, data);
                            this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + data + ",Types:String");
                        }
                    } else {
                        ps.setNull(rs + 1, type);
                        this.L.debug("\tSQLParameter:" + (rs + 1) + "=NULL,Types:" + type);
                    }
                } else {
                    if(obj == null) {
                        ps.setNull(rs + 1, 12);
                    } else {
                        ps.setObject(rs + 1, obj);
                    }

                    this.L.debug("\tSQLParameter:" + (rs + 1) + "=" + params[rs]);
                }
            }

            ResultSet var12 = ps.executeQuery();
            this.values = DBUtils.getResultSet(var12);
            long var13 = System.currentTimeMillis();
            long var14 = var13 - s;
            this.L.info("Executed(" + s + "):" + this.values.size() + " records,.............ok(" + var14 + "ms)");
            ps.close();
            var12.close();
            return this.values.size();
        }
    }

    public List<String> getStringListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getStringValue(param, i));
        }

        return li;
    }

    public List<Date> getDateListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getDateValue(param, i));
        }

        return li;
    }

    public List<Double> getDoubleListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getDoubleValue(param, i));
        }

        return li;
    }

    public List<Float> getFloatListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getFloatValue(param, i));
        }

        return li;
    }

    public List<String> getFlormatStringListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getStringValue(param, i));
        }

        return li;
    }

    public List<Long> getLongListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getLongValue(param, i));
        }

        return li;
    }

    public List<Integer> getIntegerListValue(String param) throws Exception {
        ArrayList li = new ArrayList();

        for(int i = 0; i < this.getResultCount(); ++i) {
            li.add(this.getIntegerValue(param, i));
        }

        return li;
    }
}
