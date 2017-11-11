package com.core.smart.helper;

import com.core.smart.tools.CollectionUtil;
import com.core.smart.tools.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库操作帮助类
 * Created by Administrator on 2017/11/6.
 */
public final class DatabaseHelper {
    private static  final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);



    private static final ThreadLocal<Connection> CONNECTION_HOLDER ;
    private static  final QueryRunner QUERY_RUNNER;

    private static  final BasicDataSource  DATA_SOURCE;

    static
    {
        QUERY_RUNNER = new QueryRunner();
        CONNECTION_HOLDER = new ThreadLocal<Connection>();

        Properties conf = PropsUtil.loadProps("smart.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }


    private static String  getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection conn = getConnectionByThreadLocal();
        if (conn!=null){
            try {
                conn.rollback();
                conn.close();
            }catch (SQLException e){
                LOGGER.error("rollback transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection conn = getConnectionByThreadLocal();
        if (conn!=null){
            try{
                conn.commit();
                conn.close();
            }catch (SQLException e){
                LOGGER.error("commit transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction()
    {
        Connection connection = getConnectionByThreadLocal();
        if (connection!=null){
            try{
                connection.setAutoCommit(false);
            }catch (SQLException e){
                LOGGER.error("begin transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

    }

    /**
     * 执行sql文件
     */
    public static void executeSqlFile(String filePath)
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try{
            String sql ;
            while ((sql=reader.readLine())!=null){
                DatabaseHelper.executeUpdate(sql);
            }
        }catch (Exception e){
            LOGGER.error("Execute sql file failure",e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除实体
     */
    public static <T> boolean deleteEntity(Class<T> entityClass,Long id)
    {
        String sql = "DELETE FROM "+getTableName(entityClass)+" WHERE id=?";
        LOGGER.info("sql:"+sql);
        return executeUpdate(sql,id)==1;
    }

    /**
     * 更新实体
     */
    public static <T> boolean updateEntity(Class<T> entityClass,Long id,Map<String,Object> fieldMap)
    {
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("Can not update entity: fieldMap is empty");
            return false;
        }
        String sql = "UPDATE "+ getTableName(entityClass)+ " SET ";
        StringBuilder columns = new StringBuilder();
        for(String column:fieldMap.keySet()){
            columns.append(column).append("=?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", "))+ " where id=?";
        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        LOGGER.info("sql:"+sql);
        return executeUpdate(sql,params)==1;
    }

    /**
     * 插入实体
     */
    public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap)
    {
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("Can not insert entity: fieldMap is empty");
            return false;
        }
        String sql = "INSERT INTO "+getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for(String column:fieldMap.keySet()){
            columns.append(column).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns+ "VALUES" + values;
        Object[] params = fieldMap.values().toArray();
        LOGGER.info("sql:"+sql);
        return executeUpdate(sql,params)==1;
    }


    /**
     * 执行更新语句（update,delete,insert）
     */
    public static int executeUpdate(String sql,Object... params){
        LOGGER.info("sql:"+sql);
        int rows=0;
        try{
            Connection conn = getConnectionByThreadLocal();
            rows = QUERY_RUNNER.update(conn,sql,params);
        }catch (Exception e){
            LOGGER.error("execute update failure",e);
            throw new RuntimeException(e);
        }

        return rows;
    }

    /**
     * 执行查询语句
     */
    public static List<Map<String,Object>> executeQuery(String sql,Object... params){
        LOGGER.info("sql:"+sql);
        List<Map<String,Object>> result;
        try{
            Connection conn = getConnectionByThreadLocal();
            result = QUERY_RUNNER.query(conn,sql,new MapListHandler(),params);
        }catch (Exception e){
            LOGGER.error("execute query failure",e);
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 查询实体列表(更新后，by ThreadLocal)
     */
    public static <T> List<T> queryEntityListByThreadLocal(Class<T> entityClass,String sql,Object... params ){
        LOGGER.info("sql:"+sql);
        List<T> entityList =null;
        try {
            Connection conn=getConnectionByThreadLocal();
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("Query entity list failure",e);
            throw new RuntimeException(e);
        }

        return entityList;
    }


    /**
     * 查询实体(更新后，By ThreadLocal)
     */
    public static <T> T queryEntityByThreadLocal(Class<T> entityClass,String sql,Object... params){
        LOGGER.info("sql:"+sql);
        T entity =null;
        try {
            Connection conn=getConnectionByThreadLocal();
            entity = QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("Query entity failure",e);
            throw new RuntimeException(e);
        }

        return entity;
    }


    /**
     * 获取数据库连接(更新)
     */
    public static Connection getConnectionByThreadLocal(){
        Connection conn = CONNECTION_HOLDER.get();
        try{
            conn = DATA_SOURCE.getConnection();
        }catch (SQLException e){
            LOGGER.error("get connection failure",e);
            throw new RuntimeException(e);
        }finally {
            CONNECTION_HOLDER.set(conn);
        }
        return conn;
    }


}
