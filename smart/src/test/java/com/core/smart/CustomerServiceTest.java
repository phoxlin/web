package com.core.smart;

import com.core.smart.tools.PropsUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * 测试CustomerService
 * Created by Administrator on 2017/11/9.
 */
public class CustomerServiceTest {
    @Before
    public void init(){
        Properties conf = PropsUtil.loadProps("smart.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");

    }

    @Test
    public void testGetCustomerList(){

    }

}
