package com.core.server.db;

import com.core.SFile;
import com.core.enuts.DBType;
import com.core.server.db.impl.EntityImpl;
import com.core.server.db.xml.Data;
import com.core.server.db.xml.Datas;
import com.core.server.log.Logger;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public abstract class DBUtils {
    public DBServer server;
    public static String mgo_host = "";
    public static int mgo_port = 0;
    private static ComboPooledDataSource masterDataSource = null;
    private static int slaveDataSourceIndex = 0;
    private static List<ComboPooledDataSource> slaveDataSources = new ArrayList();
    private static String dbDriver = "";
    private static String dbServer = "";
    private static String dbLogin = "";
    private static String dbPassword = "";
    private static int minConns = 0;
    private static int maxConns = 0;
    public static String dbname;
    public static String mgo_dbname = null;

    public DBUtils() {
    }

    public abstract SFile backup(String var1, Connection var2, boolean var3) throws Exception;

    public abstract SFile getback(String var1, Connection var2) throws Exception;

    public static int getPageStart(int start, int pagesize) {
        return (start - 1) * pagesize + 1;
    }

    public static int getPageEnd(int start, int pagesize) {
        return start * pagesize;
    }

    public static DBType getDBType() {
        String type = Resources.getProperty("DATABASE_TYPE", "Oracle");
        return "oracle".equalsIgnoreCase(type)?DBType.Oracle:("mysql".equalsIgnoreCase(type)?DBType.Mysql:("sqlserver".equalsIgnoreCase(type)?DBType.Sqlserver:("db2".equalsIgnoreCase(type)?DBType.DB2:("mongodb".equalsIgnoreCase(type)?DBType.MONGODB:null))));
    }

    public static DBType getRDBType() {
        String type = Resources.getProperty("JDBC_TYPE", "Oracle");
        return "oracle".equalsIgnoreCase(type)?DBType.Oracle:("mysql".equalsIgnoreCase(type)?DBType.Mysql:("sqlserver".equalsIgnoreCase(type)?DBType.Sqlserver:("db2".equalsIgnoreCase(type)?DBType.DB2:("mongodb".equalsIgnoreCase(type)?DBType.MONGODB:null))));
    }

    public static String oid() {
        ObjectId id = new ObjectId();
        return id.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String formartDate(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    public static String copyEntityOne(Entity en, String tablename, String nikename, Connection conn, Map<String, Object> values) throws Exception {
        EntityImpl entity = new EntityImpl(tablename, conn);
        if(nikename != null && nikename.length() > 0) {
            entity.setTablename(nikename);
        }

        Iterator var7 = en.getCols().iterator();

        while(var7.hasNext()) {
            Column id = (Column)var7.next();
            if(!id.getName().equalsIgnoreCase("id")) {
                entity.setValue(id.getName(), en.getValue(id.getName()));
            }
        }

        if(values != null && values.size() > 0) {
            var7 = values.entrySet().iterator();

            while(var7.hasNext()) {
                Map.Entry id1 = (Map.Entry)var7.next();
                entity.setValue((String)id1.getKey(), id1.getValue());
            }
        }

        String id2 = entity.create();
        return id2;
    }

    public static Date formartDate(String str, String format) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(str);
    }

    public static void initDB() {
        String database_type = Resources.getProperty("DATABASE_TYPE", "mysql");
        String host = Resources.getProperty("DATABASE_HOST", "localhost");
        int port = Resources.getIntProperty("DATABASE_PORT", 3306);
        String db_name = Resources.getProperty("DATABASE_NAME", "frame");
        mgo_host = Resources.getProperty("MONGODB_HOST", "localhost");
        String mongodb_name = Resources.getProperty("MONGODB_NAME", "frame");
        dbname = db_name.split("\\?")[0];
        mgo_dbname = mongodb_name.split("\\?")[0];
        mgo_port = Resources.getIntProperty("MONGODB_PORT", 27017);
        if(db_name != null && db_name.length() > 0) {
            dbDriver = "com.mysql.jdbc.Driver";
            dbServer = "jdbc:mysql://" + host + ":" + port + "/" + db_name;
            if("oracle".equalsIgnoreCase(database_type)) {
                dbServer = "jdbc:oracle:thin:@" + host + ":" + port + ":" + db_name;
                dbDriver = "oracle.jdbc.driver.OracleDriver";
            } else if("db2".equalsIgnoreCase(database_type)) {
                dbServer = "jdbc:mysql://" + host + ":" + port + "/" + db_name;
                dbDriver = "com.mysql.jdbc.Driver";
                Logger.error("DB2 jdbc 没有定义.");
            } else if("sqlserver".equalsIgnoreCase(database_type)) {
                dbServer = "jdbc:mysql://" + host + ":" + port + "/" + db_name;
                dbDriver = "com.mysql.jdbc.Driver";
                Logger.error("SqlServer jdbc 没有定义.");
            }

            dbLogin = Resources.getProperty("LOGIN_ID");
            dbPassword = Resources.getProperty("LOGIN_PASSWORD");
            String tmp = Resources.getProperty("MIN_CONNECTION");

            try {
                if(tmp != null && tmp.trim().length() > 0) {
                    minConns = Integer.parseInt(tmp);
                }
            } catch (Exception var16) {
                ;
            }

            tmp = Resources.getProperty("MAX_CONNECTION");

            try {
                if(tmp != null && tmp.trim().length() > 0) {
                    maxConns = Integer.parseInt(tmp);
                }
            } catch (Exception var15) {
                ;
            }

            try {
                masterDataSource = new ComboPooledDataSource();
                masterDataSource.setDriverClass(dbDriver);
                masterDataSource.setMaxPoolSize(maxConns);
                masterDataSource.setMinPoolSize(minConns);
                masterDataSource.setInitialPoolSize(maxConns / 3);
                masterDataSource.setMaxIdleTime(60);
                masterDataSource.setAutomaticTestTable("sys_testdb");
                masterDataSource.setTestConnectionOnCheckin(true);
                masterDataSource.setTestConnectionOnCheckout(true);
                masterDataSource.setIdleConnectionTestPeriod(18000);
                masterDataSource.setAutoCommitOnClose(true);
                masterDataSource.setAcquireIncrement(3);
                masterDataSource.setCheckoutTimeout(0);
                masterDataSource.setAcquireRetryAttempts(30);
                masterDataSource.setMaxStatements(0);
                masterDataSource.setMaxStatementsPerConnection(0);
                masterDataSource.setJdbcUrl(dbServer);
                masterDataSource.setUser(dbLogin);
                masterDataSource.setPassword(dbPassword);
                List e = Resources.getListProperty("Slave.DATABASE_HOST");
                List slavePorts = Resources.getListProperty("Slave.DATABASE_PORT");
                List slaveDatabaseNames = Resources.getListProperty("Slave.DATABASE_NAME");
                List slaveLoginNames = Resources.getListProperty("Slave.LOGIN_ID");
                List slavePwds = Resources.getListProperty("Slave.LOGIN_PASSWORD");
                if(e.size() > 0 && e.size() == slavePorts.size() && e.size() == slaveDatabaseNames.size() && e.size() == slaveLoginNames.size() && e.size() == slavePwds.size()) {
                    Logger.debug("Found slave DB settings, initialize......................");

                    for(int i = 0; i < e.size(); ++i) {
                        String slaveDbServer = "jdbc:mysql://" + (String)e.get(i) + ":" + (String)slavePorts.get(i) + "/" + (String)slaveDatabaseNames.get(i);
                        String slaveDbDriver = "com.mysql.jdbc.Driver";
                        if("oracle".equalsIgnoreCase(database_type)) {
                            slaveDbServer = "jdbc:oracle:thin:@" + (String)e.get(i) + ":" + (String)slavePorts.get(i) + ":" + (String)slaveDatabaseNames.get(i);
                            slaveDbDriver = "oracle.jdbc.driver.OracleDriver";
                        } else if("db2".equalsIgnoreCase(database_type)) {
                            Logger.error("DB2 jdbc 没有定义.");
                        } else if("sqlserver".equalsIgnoreCase(database_type)) {
                            Logger.error("SqlServer jdbc 没有定义.");
                        }

                        ComboPooledDataSource slaveDataSource = new ComboPooledDataSource();
                        slaveDataSource.setDriverClass(slaveDbDriver);
                        slaveDataSource.setMaxPoolSize(maxConns);
                        slaveDataSource.setMinPoolSize(minConns);
                        slaveDataSource.setInitialPoolSize(maxConns / 3);
                        slaveDataSource.setMaxIdleTime(60);
                        slaveDataSource.setAutomaticTestTable("sys_testdb");
                        slaveDataSource.setTestConnectionOnCheckin(true);
                        slaveDataSource.setTestConnectionOnCheckout(true);
                        slaveDataSource.setIdleConnectionTestPeriod(18000);
                        slaveDataSource.setAutoCommitOnClose(true);
                        slaveDataSource.setAcquireIncrement(3);
                        slaveDataSource.setCheckoutTimeout(0);
                        slaveDataSource.setAcquireRetryAttempts(30);
                        slaveDataSource.setMaxStatements(0);
                        slaveDataSource.setMaxStatementsPerConnection(0);
                        slaveDataSource.setJdbcUrl(slaveDbServer);
                        slaveDataSource.setUser((String)slaveLoginNames.get(i));
                        slaveDataSource.setPassword((String)slavePwds.get(i));
                        slaveDataSources.add(slaveDataSource);
                    }

                    Logger.debug("Initialized slave DB(" + e.size() + ") ......................");
                }

                Logger.debug("Initialized DB.............");
            } catch (Exception var17) {
                Logger.error("Initializ DB failed:\t" + var17);
            }
        }

    }

    public static Connection getConnection() throws Exception {
        if(masterDataSource == null) {
            initDB();
        }

        Connection conn = null;

        try {
            conn = masterDataSource.getConnection();
            conn.setTransactionIsolation(2);
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException var2) {
            throw new Exception("Can\'t get DB connection:" + var2);
        }
    }

    public static Connection getSlaveConnection() throws Exception {
        if(slaveDataSources.size() <= 0) {
            return getConnection();
        } else {
            Connection conn = null;
            if(slaveDataSources.size() <= 1) {
                try {
                    conn = ((ComboPooledDataSource)slaveDataSources.get(0)).getConnection();
                    conn.setTransactionIsolation(2);
                    return conn;
                } catch (SQLException var2) {
                    return getConnection();
                }
            } else {
                if(slaveDataSourceIndex >= slaveDataSources.size()) {
                    slaveDataSourceIndex = 0;
                }

                while(slaveDataSourceIndex < slaveDataSources.size()) {
                    try {
                        conn = ((ComboPooledDataSource)slaveDataSources.get(slaveDataSourceIndex)).getConnection();
                        conn.setTransactionIsolation(2);
                        ++slaveDataSourceIndex;
                        return conn;
                    } catch (SQLException var3) {
                        ;
                    }
                }

                return getConnection();
            }
        }
    }

    public static void freeConnection(Connection conn) {
        if(conn != null) {
            try {
                if(conn != null) {
                    conn.close();
                }
            } catch (Exception var2) {
                Logger.error(var2);
            }
        }

        conn = null;
    }

    public static List<Map<String, Object>> getResultSet(ResultSet rs) throws SQLException {
        ArrayList values = new ArrayList();
        ResultSetMetaData data = rs.getMetaData();

        while(rs.next()) {
            HashMap map = new HashMap();

            for(int i = 1; i <= data.getColumnCount(); ++i) {
                String key = data.getColumnLabel(i).toLowerCase();
                Object val = rs.getObject(i);
                if(val != null && val.getClass().isArray() && !(val instanceof byte[])) {
                    val = rs.getString(i);
                }

                map.put(key, val);
            }

            values.add(map);
        }

        return values;
    }

    public void initBakDB(Connection conn) throws Exception {
        EntityImpl sys_db_bak_set = new EntityImpl(conn);
        int size = sys_db_bak_set.executeQuery("select * from sys_db_bak_set");
        if(size > 0) {
            this.server = new DBServer();
            String rule = sys_db_bak_set.getStringValue("rule");
            this.server.setRule(rule);
            this.server.setZip(sys_db_bak_set.getBooleanValue("zip"));
            this.server.setSaveType(sys_db_bak_set.getStringValue("save_type"));
            this.server.setSaveVal(sys_db_bak_set.getIntegerValue("save_val").intValue());
        } else {
            throw new Exception("没有设置备份数据库信息");
        }
    }

    public static void createTable2System(Map<String, List<String>> tableInfos, Map<String, String> dataInfos, Connection conn) throws Exception {
        EntityImpl entity = new EntityImpl(conn);
        Iterator var5 = tableInfos.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry en = (Map.Entry)var5.next();
            String tableid = (String)en.getKey();
            List columnIds = (List)en.getValue();
            String tablecode = ((String)dataInfos.get(tableid + "tablecode")).toLowerCase();
            String tablename = (String)dataInfos.get(tableid + "tablename");

            try {
                entity.executeUpdate("delete from sys_table where table_code = \'" + tablecode + "\'");
            } catch (Exception var20) {
                ;
            }

            if(columnIds != null && columnIds.size() > 0) {
                int sort = 1;

                EntityImpl sys_table;
                for(Iterator var12 = columnIds.iterator(); var12.hasNext(); sys_table.create()) {
                    String id = (String)var12.next();
                    String code = ((String)dataInfos.get(id + "colcode")).toLowerCase();
                    String datatype = ((String)dataInfos.get(id + "datatype")).toLowerCase();
                    String length = ((String)dataInfos.get(id + "length")).toLowerCase();
                    String mandatory = (String)dataInfos.get(id + "mandatory");
                    String comment = (String)dataInfos.get(id + "comment");
                    String primarykey = (String)dataInfos.get(id + "primarykey");
                    sys_table = new EntityImpl("sys_table", conn);
                    sys_table.setValue("TABLE_CODE", tablecode);
                    sys_table.setValue("TABLE_NAME", tablename);
                    sys_table.setValue("XML_FIELD_NAME", code);
                    sys_table.setValue("DB_FIELD_NAME", code);
                    sys_table.setValue("DB_FIELD_COMMENT", comment);
                    sys_table.setValue("C_TYPE", datatype);
                    sys_table.setValue("NULLABLE", "Y".equalsIgnoreCase(mandatory)?"false":"true");
                    sys_table.setValue("LENGTH", length);
                    sys_table.setValue("SORT", Integer.valueOf(sort++));
                    if("Y".equalsIgnoreCase(primarykey)) {
                        sys_table.setValue("IS_KEY", "true");
                        sys_table.setValue("IS_AUTO_GEN", "true");
                    } else {
                        sys_table.setValue("IS_KEY", "false");
                        sys_table.setValue("IS_AUTO_GEN", "false");
                    }
                }
            }
        }

    }

    public static void createDBTable(String tablename, Connection conn) throws Exception {
        if(tablename != null && tablename.length() > 0) {
            EntityImpl en = new EntityImpl(conn);
            int size = en.executeQuery("select * from sys_table a where a.table_code=\'" + tablename + "\' order by a.sort");
            if(size <= 0) {
                throw new Exception("数据库里面没有找到表名：" + tablename + " 的设置");
            } else {
                String tablecomment = en.getStringValue("table_name");
                String droptablesql;
                String i;
                String comment;
                String c_type;
                if(getRDBType() == DBType.Mysql) {
                    boolean sqls = false;
                    StringBuilder hasPId = new StringBuilder("create table " + tablename + " (");

                    for(int sb = 0; sb < size; ++sb) {
                        droptablesql = en.getStringValue("XML_FIELD_NAME", sb);
                        i = en.getStringValue("DB_FIELD_COMMENT", sb);
                        comment = en.getStringValue("c_type", sb);
                        c_type = en.getStringValue("NULLABLE", sb);
                        int nullable = en.getIntegerValue("LENGTH", sb).intValue();
                        String length = en.getStringValue("IS_KEY", sb);
                        hasPId.append(droptablesql);
                        if("long".equalsIgnoreCase(comment)) {
                            hasPId.append("  INT");
                        } else if("datetime".equalsIgnoreCase(comment)) {
                            hasPId.append("  datetime");
                        } else if("date".equalsIgnoreCase(comment)) {
                            hasPId.append("  date");
                        } else if("float".equalsIgnoreCase(comment)) {
                            hasPId.append("  float");
                        } else if("text".equalsIgnoreCase(comment)) {
                            hasPId.append("  TEXT");
                        } else if("mediumtext".equalsIgnoreCase(comment)) {
                            hasPId.append("  MEDIUMTEXT");
                        } else if("longtext".equalsIgnoreCase(comment)) {
                            hasPId.append("  LONGTEXT");
                        } else if(nullable == -1) {
                            hasPId.append("  VARCHAR(100)");
                        } else {
                            hasPId.append("  VARCHAR(" + nullable + ")");
                        }

                        if("false".equalsIgnoreCase(c_type)) {
                            hasPId.append(" not null");
                        }

                        hasPId.append(" comment \'" + i + "\',");
                        if("true".equalsIgnoreCase(length)) {
                            if(!"id".equalsIgnoreCase(droptablesql)) {
                                throw new Exception("表名：" + tablename + " 主键必须定义为：ID");
                            }

                            sqls = true;
                        }
                    }

                    if(!sqls) {
                        throw new Exception("表名：" + tablename + " 没有定义：主键");
                    }

                    hasPId.append("primary key (ID)");
                    hasPId.append(") DEFAULT CHARSET=utf8");
                    String var18 = " drop table if exists " + tablename;
                    en.executeUpdate(var18);
                    en.executeUpdate(hasPId.toString());
                    if(tablecomment != null && tablecomment.length() > 0) {
                        en.executeUpdate("alter table " + tablename + " comment \'" + tablecomment + "\'");
                    }
                } else {
                    if(getRDBType() != DBType.Oracle) {
                        throw new Exception("暂时没有实现数据库类型【" + getRDBType() + "】的DDL方法");
                    }

                    ArrayList var16 = new ArrayList();
                    var16.add("comment on table " + tablename + " is\'" + tablecomment + "\'");
                    boolean var17 = false;
                    StringBuilder var19 = new StringBuilder("create table " + tablename + " (");

                    for(int var20 = 0; var20 < size; ++var20) {
                        i = en.getStringValue("XML_FIELD_NAME", var20);
                        comment = en.getStringValue("DB_FIELD_COMMENT", var20);
                        c_type = en.getStringValue("c_type", var20);
                        String var22 = en.getStringValue("NULLABLE", var20);
                        int var23 = en.getIntegerValue("LENGTH", var20).intValue();
                        String is_key = en.getStringValue("IS_KEY", var20);
                        var19.append(i);
                        if("long".equalsIgnoreCase(c_type)) {
                            var19.append("  INTEGER");
                        } else if("datetime".equalsIgnoreCase(c_type)) {
                            var19.append("  datetime");
                        } else if("date".equalsIgnoreCase(c_type)) {
                            var19.append("  date");
                        } else if("float".equalsIgnoreCase(c_type)) {
                            var19.append("  float");
                        } else {
                            var19.append("  VARCHAR2(" + var23 + ")");
                        }

                        if("false".equalsIgnoreCase(var22)) {
                            var19.append(" not null,");
                        } else {
                            var19.append(" ,");
                        }

                        var16.add("comment on column " + tablename + "." + i + " is\'" + comment + "\'");
                        if("true".equalsIgnoreCase(is_key)) {
                            if(!"id".equalsIgnoreCase(i)) {
                                throw new Exception("表名：" + tablename + " 主键必须定义为：ID");
                            }

                            var17 = true;
                        }
                    }

                    if(!var17) {
                        throw new Exception("表名：" + tablename + " 没有定义：主键");
                    }

                    var19.append("constraint PK_" + tablename + " primary key (ID)");
                    var19.append(" )");
                    droptablesql = "drop table " + tablename + " cascade constraints";

                    try {
                        en.executeUpdate(droptablesql);
                    } catch (Exception var15) {
                        ;
                    }

                    en.executeUpdate(var19.toString());

                    for(int var21 = 0; var21 < var16.size(); ++var21) {
                        en.executeUpdate((String)var16.get(var21));
                    }
                }

            }
        } else {
            throw new Exception("没有找到表名：" + tablename + " 的设置");
        }
    }

    public static void createEntityXml(String tablename, Connection conn) throws Exception {
        String path = Resources.getProperty("workspace", Utils.getWebRootPath()) + "WEB-INF/configs/models";
        if(tablename != null && tablename.length() > 0) {
            EntityImpl en = new EntityImpl(conn);
            int size = en.executeQuery("select * from sys_table a where a.table_code=\'" + tablename + "\' order by a.sort");
            if(size > 0) {
                String tablecomment = en.getStringValue("table_name");
                Document root = DocumentHelper.createDocument();
                Element table = root.addElement("table");
                String tablecode = tablename.toLowerCase();
                table.addAttribute("name", tablecode);
                table.addAttribute("comment", tablecomment);
                Element cols = table.addElement("columns");

                for(int format = 0; format < size; ++format) {
                    Element writer = cols.addElement("column");
                    String code = en.getStringValue("XML_FIELD_NAME", format);
                    String field = en.getStringValue("DB_FIELD_NAME", format);
                    String comment = en.getStringValue("DB_FIELD_COMMENT", format);
                    String datatype = en.getStringValue("c_type", format);
                    String mandatory = en.getStringValue("NULLABLE", format);
                    int length = en.getIntegerValue("LENGTH", format).intValue();
                    String primarykey = en.getStringValue("IS_KEY", format);
                    String IS_AUTO_GEN = en.getStringValue("IS_AUTO_GEN", format);
                    writer.addAttribute("name", code);
                    writer.addAttribute("field", field);
                    writer.addAttribute("type", datatype);
                    if("date".equalsIgnoreCase(datatype)) {
                        writer.addAttribute("format", "yyyy-MM-dd");
                    } else if("datetime".equalsIgnoreCase(datatype)) {
                        writer.addAttribute("format", "yyyy-MM-dd HH:mm:ss");
                    }

                    writer.addAttribute("comment", comment);
                    if(length != 0) {
                        writer.addAttribute("length", String.valueOf(length));
                    }

                    writer.addAttribute("nullable", mandatory);
                    if("TRUE".equalsIgnoreCase(primarykey)) {
                        writer.addAttribute("iskey", "true");
                        writer.addAttribute("isAutoGenerate", IS_AUTO_GEN);
                    }
                }

                OutputFormat var21 = OutputFormat.createPrettyPrint();
                XMLWriter var22 = new XMLWriter(new FileWriter(path + "/" + tablecode + ".xml"), var21);
                var22.write(root);
                var22.close();
                Logger.info("Created xml file:" + path + "/" + tablecode + ".xml");

                try {
                    FileUtils.copyFile(new File(path + "/" + tablecode + ".xml"), new File(Utils.getWebRootPath() + "WEB-INF/configs/models" + "/" + tablecode + ".xml"));
                } catch (Exception var20) {
                    ;
                }

            } else {
                throw new Exception("数据库里面没有找到表名：" + tablename + " 的设置");
            }
        } else {
            throw new Exception("没有找到表名：" + tablename + " 的设置");
        }
    }

    public static void main(String[] args) throws Exception {
        Data d = Datas.findOne("sys_apps", "id", "=", "1233");
        System.out.println(d.getStringValue("name"));
    }
}
