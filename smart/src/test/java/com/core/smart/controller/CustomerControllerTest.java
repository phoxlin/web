package com.core.smart.controller;

import com.core.smart.tools.PropsUtil;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 测试CustomerService
 * Created by Administrator on 2017/11/9.
 */
public class CustomerControllerTest {

    private static  Connection conn ;

    @Before
    public void init(){
        Properties conf = PropsUtil.loadProps("smart.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");
        try{
            conn = DriverManager.getConnection(url,username,password);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testGetCustomerList(){

    }

}
