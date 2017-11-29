package com.core.server.designer;

import com.core.enuts.DBType;
import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.db.DBUtils;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.task.TaskDesignerUtils;
import com.core.server.tools.NettyUtils;
import com.core.server.tools.Utils;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DesignerDBAction extends BasicAction {

    @Route(
            value = "/designer-init-dblist",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void initDBList() throws Exception {
        EntityImpl sys_db_designer = new EntityImpl(this);
        int size = sys_db_designer.executeQuery("select id,tabletype,tablename,tablecode from sys_db_designer order by tablecode,tablename");
        this.obj.put("size", size);
        JSONArray li = new JSONArray();

        for(int i = 0; i < size; ++i) {
            String _id = sys_db_designer.getStringValue("id", i);
            String tabletype = sys_db_designer.getStringValue("tabletype", i);
            String tablename = sys_db_designer.getStringValue("tablename", i);
            String tablecode = sys_db_designer.getStringValue("tablecode", i);
            JSONObject o = new JSONObject();
            o.put("_id", _id);
            o.put("tabletype", tabletype);
            o.put("tablename", tablename);
            o.put("tablecode", tablecode);
            li.put(o);
        }

        this.obj.put("db", li);
    }

    @Route(
            value = "/designer-recreate-tableDB",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void reCreateTableDB() throws Exception {
        String tablename = this.request.getParameter("tablename");
        String tablecode = this.request.getParameter("tablecode").toLowerCase();
        String layoutdata = this.request.getParameter("layoutdata");
        List list = DesDBUtils.parseColumnLayoutData(layoutdata);
        if(list != null && list.size() > 0) {
            StringBuffer createMysqlTableSql = new StringBuffer("create table " + tablecode + " (\n");
            StringBuffer createOracleTableSql = new StringBuffer("create table " + tablecode + " (\n");
            ArrayList os = new ArrayList();
            boolean hasKey = false;

            String o;
            String comment;
            for(Iterator droptablesql = list.iterator(); droptablesql.hasNext(); os.add("comment on column " + tablecode + "." + o + " is\'" + comment + "\'")) {
                Map en = (Map)droptablesql.next();
                o = Utils.getMapStringValue(en, "code").toUpperCase();
                comment = Utils.getMapStringValue(en, "name");
                boolean nullable = Utils.getMapBooleanValue(en, "nullable");
                String length = Utils.getMapStringValue(en, "length");
                if("id".equalsIgnoreCase(o)) {
                    hasKey = true;
                }

                String controlType = Utils.getMapStringValue(en, "controlType");
                if(!"validatebox".equals(controlType) && !"password".equals(controlType)) {
                    if("numberbox".equals(controlType)) {
                        createMysqlTableSql.append(o + "  VARCHAR(" + 20 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                        createOracleTableSql.append(o + "  VARCHAR2(" + 20 + ") " + (nullable?"not null":"") + ",\n");
                    } else if("datebox".equals(controlType)) {
                        createMysqlTableSql.append(o + "  date " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                        createOracleTableSql.append(o + " date " + (nullable?"not null":"") + ",\n");
                    } else if("datetimebox".equals(controlType)) {
                        createMysqlTableSql.append(o + "  datetime " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                        createOracleTableSql.append(o + " date " + (nullable?"not null":"") + ",\n");
                    } else if(!"select".equals(controlType) && !"tree".equals(controlType)) {
                        if("upload".equals(controlType)) {
                            createMysqlTableSql.append(o + "  VARCHAR(" + 200 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + "  VARCHAR2(" + 200 + ") " + (nullable?"not null":"") + ",\n");
                        } else if("uploadFile".equals(controlType)) {
                            createMysqlTableSql.append(o + "  VARCHAR(" + 32 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + "  VARCHAR2(" + 32 + ") " + (nullable?"not null":"") + ",\n");
                        } else if("editor".equals(controlType)) {
                            createMysqlTableSql.append(o + "  LONGTEXT " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + " clob " + (nullable?"not null":"") + ",\n");
                        } else if("area_text".equals(controlType)) {
                            createMysqlTableSql.append(o + "  TEXT " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + " clob " + (nullable?"not null":"") + ",\n");
                        } else if("timespinner".equals(controlType)) {
                            createMysqlTableSql.append(o + "  VARCHAR(" + 10 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + "  VARCHAR2(" + 10 + ") " + (nullable?"not null":"") + ",\n");
                        } else if("numberspinner".equals(controlType)) {
                            createMysqlTableSql.append(o + "  VARCHAR(" + 20 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                            createOracleTableSql.append(o + "  VARCHAR2(" + 20 + ") " + (nullable?"not null":"") + ",\n");
                        }
                    } else {
                        createMysqlTableSql.append(o + "  VARCHAR(" + 20 + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                        createOracleTableSql.append(o + "  VARCHAR2(" + 20 + ") " + (nullable?"not null":"") + ",\n");
                    }
                } else {
                    createMysqlTableSql.append(o + "  VARCHAR(" + length + ") " + (nullable?"not null":"") + "  comment \'" + comment + "\',\n");
                    createOracleTableSql.append(o + "  VARCHAR2(" + length + ") " + (nullable?"not null":"") + ",\n");
                }
            }

            os.add("comment on table " + tablecode + " is\'" + tablename + "\'");
            if(!hasKey) {
                throw new Exception("表名：" + tablename + " 没有定义：主键");
            }

            if(DBUtils.getRDBType() == DBType.Mysql) {
                createMysqlTableSql.append("primary key (ID)\n");
            } else if(DBUtils.getRDBType() == DBType.Oracle) {
                createOracleTableSql.append("constraint PK_" + tablecode + " primary key (ID)");
            }

            createMysqlTableSql.append(" )");
            EntityImpl en1 = new EntityImpl(this);
            String droptablesql1;
            if(DBUtils.getRDBType() == DBType.Mysql) {
                droptablesql1 = " drop table if exists " + tablecode;
                en1.executeUpdate(droptablesql1);
                en1.executeUpdate(createMysqlTableSql.toString());
                if(tablename != null && tablename.length() > 0) {
                    en1.executeUpdate("alter table " + tablecode + " comment \'" + tablename + "\'");
                }
            } else if(DBUtils.getRDBType() == DBType.Oracle) {
                droptablesql1 = "drop table " + tablename + " cascade constraints";
                en1.executeUpdate(droptablesql1);
                en1.executeUpdate(createOracleTableSql.toString());
                Iterator comment1 = os.iterator();

                while(comment1.hasNext()) {
                    o = (String)comment1.next();
                    en1.executeUpdate(o);
                }
            }
        }

    }

    @Route(
            value = "/designer-recreate-tablecfg",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void reCreateCfg() throws Exception {
        String tabletype = "R";
        String tablename = this.request.getParameter("tablename");
        String tablecode = this.request.getParameter("tablecode").toLowerCase();
        String layoutdata = this.request.getParameter("layoutdata");
        Document doc = DocumentHelper.createDocument();
        Element columns = doc.addElement("table").addAttribute("name", tablecode).addAttribute("comment", tablename);
        List list = DesDBUtils.parseColumnLayoutData(layoutdata);
        if(list != null && list.size() > 0) {
            Iterator file = list.iterator();

            while(file.hasNext()) {
                Map format = (Map)file.next();
                String xmlFile = Utils.getMapStringValue(format, "code").toLowerCase();
                String descFile = Utils.getMapStringValue(format, "bindtype");
                String writer = Utils.getMapStringValue(format, "binddata");
                String bindtype = TaskDesignerUtils.parseBindType(descFile);
                String type = "string";
                String controlType = Utils.getMapStringValue(format, "controlType");
                String length = Utils.getMapStringValue(format, "length");
                if("validatebox".equals(controlType)) {
                    type = "string";
                } else if("password".equals(controlType)) {
                    type = "string";
                } else if("numberbox".equals(controlType)) {
                    type = "string";
                    length = "20";
                } else if("datebox".equals(controlType)) {
                    type = "date";
                    length = "";
                } else if("datetimebox".equals(controlType)) {
                    type = "datetime";
                    length = "";
                } else if("select".equals(controlType)) {
                    type = "string";
                    length = "20";
                } else if("tree".equals(controlType)) {
                    type = "string";
                    length = "20";
                } else if("upload".equals(controlType)) {
                    type = "string";
                    length = "400";
                } else if("uploadFile".equals(controlType)) {
                    type = "string";
                    length = "32";
                } else if("editor".equals(controlType)) {
                    type = "text";
                    length = "-1";
                } else if("area_text".equals(controlType)) {
                    type = "text";
                    length = "-1";
                } else if("timespinner".equals(controlType)) {
                    type = "string";
                    length = "10";
                } else if("numberspinner".equals(controlType)) {
                    type = "string";
                    length = "20";
                } else if("chooseUser".equals(controlType)) {
                    type = "string";
                    length = "32";
                }

                String controlType2 = TaskDesignerUtils.parseControlType(controlType);
                Element item = columns.addElement("column");
                item.addAttribute("name", xmlFile);
                item.addAttribute("field", xmlFile);
                item.addAttribute("type", type);
                item.addAttribute("comment", Utils.getMapStringValue(format, "name"));
                if(length != null && length.length() > 0) {
                    item.addAttribute("length", length);
                }

                item.addAttribute("nullable", String.valueOf(!Utils.getMapBooleanValue(format, "nullable")));
                if(!"no".equals(bindtype) && writer != null && writer.length() > 0) {
                    item.addAttribute("bindType", bindtype);
                    item.addAttribute("bindData", writer);
                }

                item.addAttribute("controlType", controlType2);
                String input_tablename = Utils.getMapStringValue(format, "input_tablename");
                String width = Utils.getMapStringValue(format, "width");
                String min = Utils.getMapStringValue(format, "min");
                String max = Utils.getMapStringValue(format, "max");
                String decamial = Utils.getMapStringValue(format, "decamial");
                String otherset = Utils.getMapStringValue(format, "otherset");
                String placeholder = Utils.getMapStringValue(format, "placeholder");
                String search_compare = Utils.getMapStringValue(format, "search_compare");
                item.addAttribute("input_tablename", input_tablename);
                item.addAttribute("width", width);
                item.addAttribute("min", min);
                item.addAttribute("max", max);
                item.addAttribute("decamial", decamial);
                item.addAttribute("otherset", otherset);
                item.addAttribute("placeholder", placeholder);
                item.addAttribute("search_compare", search_compare);
                if("id".equals(xmlFile)) {
                    item.addAttribute("iskey", "true");
                    item.addAttribute("isAutoGenerate", "true");
                }
            }
        }

        OutputFormat format1 = OutputFormat.createPrettyPrint();
        File file1 = new File(NettyUtils.getRootContent() + "/configures/database/mgo");
        if("R".equals(tabletype)) {
            file1 = new File(NettyUtils.getRootContent() + "/configures/database/rdb");
        }

        if(!file1.exists()) {
            file1.mkdirs();
        }

        File xmlFile1 = new File(file1.getAbsolutePath() + "/" + tablecode + "-" + tablename + ".xml");
        File descFile1 = new File(Utils.getWebRootPath2() + "WEB-INF/configures/database/mgo" + "/" + tablecode + "-" + tablename + ".xml");
        if("R".equals(tabletype)) {
            descFile1 = new File(Utils.getWebRootPath2() + "WEB-INF/configures/database/rdb" + "/" + tablecode + "-" + tablename + ".xml");
        }

        XMLWriter writer1 = null;

        try {
            writer1 = new XMLWriter(new FileWriter(xmlFile1), format1);
            writer1.write(doc);
        } catch (Exception var31) {
            ;
        } finally {
            if(writer1 != null) {
                writer1.close();
            }

        }

        Logger.info("Created xml file:" + xmlFile1.getAbsolutePath());

        try {
            FileUtils.copyFile(xmlFile1, descFile1);
            Logger.info("Copyed xml file to:" + descFile1.getAbsolutePath());
        } catch (Exception var30) {
            ;
        }

    }

    @Route(
            value = "/designer-save-dbset",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void saveDbSet() throws Exception {
        String tabletype = this.request.getParameter("tabletype");
        String tablename = this.request.getParameter("tablename");
        String tablecode = this.request.getParameter("tablecode");
        String layoutdata = this.request.getParameter("layoutdata");
        EntityImpl sys_db_designer = new EntityImpl(this);
        int size = sys_db_designer.executeQuery("select * from sys_db_designer where tablecode =?", new String[]{tablecode});
        if(size > 0) {
            String sys_db_designer1 = sys_db_designer.getStringValue("id");
            sys_db_designer.executeUpdate("update sys_db_designer set tabletype=?,tablename=?,layoutdata=? where id=?", new String[]{tabletype, tablename, layoutdata, sys_db_designer1});
        } else {
            EntityImpl sys_db_designer11 = new EntityImpl("sys_db_designer", this);
            sys_db_designer11.setValue("tabletype", tabletype);
            sys_db_designer11.setValue("tablename", tablename);
            sys_db_designer11.setValue("tablecode", tablecode);
            sys_db_designer11.setValue("layoutdata", layoutdata);
            sys_db_designer11.setValue("createtime", new Date());
            sys_db_designer11.create();
        }

    }

    @Route(
            value = "/designer-load-dbset",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void loadDbSet() throws Exception {
        String _id = this.request.getParameter("_id");
        EntityImpl sys_db_designer1 = new EntityImpl("sys_db_designer", this);
        sys_db_designer1.setValue("id", _id);
        int size = sys_db_designer1.search();
        if(size > 0) {
            String tabletype = sys_db_designer1.getStringValue("tabletype");
            String tablename = sys_db_designer1.getStringValue("tablename");
            String tablecode = sys_db_designer1.getStringValue("tablecode");
            String layoutdata = sys_db_designer1.getStringValue("layoutdata");
            this.obj.put("_id", _id);
            this.obj.put("tabletype", tabletype);
            this.obj.put("tablename", tablename);
            this.obj.put("tablecode", tablecode);
            this.obj.put("layoutdata", layoutdata);
        }

    }
}
