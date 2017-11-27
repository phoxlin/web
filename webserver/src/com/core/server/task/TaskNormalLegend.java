package com.core.server.task;

import com.core.User;
import com.core.server.db.Column;
import com.core.server.tools.Utils;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskNormalLegend {
    private boolean rdb;
    private String type;
    private String code;
    private String name;
    private String entity;
    private String initSql;
    private boolean autoLoadingData;
    private List<TaskRow> rows = new ArrayList();

    public JSONObject toJson(User user) {
        JSONObject o = new JSONObject();
        o.put("rdb", this.isRdb());
        o.put("entity", this.getEntity());
        o.put("type", this.getType());
        o.put("code", this.getCode());
        o.put("name", this.getName());
        o.put("initSql", this.getInitSql());
        o.put("autoLoadingData", this.isAutoLoadingData());
        JSONArray rows = new JSONArray();
        Iterator var5 = this.rows.iterator();

        while(var5.hasNext()) {
            TaskRow row = (TaskRow)var5.next();
            rows.put(row.toJson(user));
        }

        o.put("rows", rows);
        return o;
    }

    public TaskNormalLegend(Element ele) {
        this.rdb = Utils.isTrue(ele.attributeValue("rdb"));
        this.type = ele.attributeValue("type");
        this.code = ele.attributeValue("code");
        this.name = ele.attributeValue("name");
        this.entity = ele.attributeValue("entity");
        this.autoLoadingData = Utils.isTrue(ele.attributeValue("autoLoadingData"));
        this.initSql = ele.attributeValue("initSql");
        List rowList = ele.selectNodes(".//row");
        if(rowList != null && rowList.size() > 0) {
            Iterator var4 = rowList.iterator();

            while(var4.hasNext()) {
                Element rowEle = (Element)var4.next();
                TaskRow row = new TaskRow();
                this.rows.add(row);
                List colList = rowEle.selectNodes(".//column");
                if(colList != null && colList.size() > 0) {
                    Iterator var8 = colList.iterator();

                    while(var8.hasNext()) {
                        Element col = (Element)var8.next();
                        row.addCol(TaskDesignerUtils.createColumn(col));
                    }
                }
            }
        }

    }

    public boolean isRdb() {
        return this.rdb;
    }

    public void setRdb(boolean rdb) {
        this.rdb = rdb;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoLoadingData() {
        return this.autoLoadingData;
    }

    public void setAutoLoadingData(boolean autoLoadingData) {
        this.autoLoadingData = autoLoadingData;
    }

    public List<TaskRow> getRows() throws Exception {
        return this.rows;
    }

    public void addRow(TaskRow row) throws Exception {
        this.rows.add(row);
    }

    public String getInitSql() {
        return this.initSql;
    }

    public void setInitSql(String initSql) {
        this.initSql = initSql;
    }

    public String getEntity() {
        return this.entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Map<String, List<Column>> getFieldColumns() throws Exception {
        HashMap m = new HashMap();
        HashSet names = new HashSet();
        Iterator var4 = this.getRows().iterator();

        while(var4.hasNext()) {
            TaskRow row = (TaskRow)var4.next();
            Iterator var6 = row.getCols().iterator();

            while(var6.hasNext()) {
                Column col = (Column)var6.next();
                Object li = (List)m.get(col.getInput_tablename());
                if(li == null) {
                    li = new ArrayList();
                    m.put(col.getInput_tablename(), li);
                }

                String x = col.getInput_tablename() + col.getName();
                if(!names.contains(x)) {
                    ((List)li).add(col);
                    names.add(x);
                }
            }
        }

        return m;
    }

    public Set<String> getTableNames() throws Exception {
        HashSet tables = new HashSet();
        Iterator var3 = this.getRows().iterator();

        while(var3.hasNext()) {
            TaskRow row = (TaskRow)var3.next();
            Iterator var5 = row.getCols().iterator();

            while(var5.hasNext()) {
                Column col = (Column)var5.next();
                if(col.getInput_tablename() != null && col.getInput_tablename().length() > 0) {
                    tables.add(col.getInput_tablename());
                }
            }
        }

        return tables;
    }
}
