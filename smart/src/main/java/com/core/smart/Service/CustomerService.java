package com.core.smart.Service;

import com.core.smart.annotation.Service;
import com.core.smart.annotation.Transaction;
import com.core.smart.helper.DatabaseHelper;
import com.core.smart.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * 客户服务类
 * Created by Administrator on 2017/11/9.
 */
@Service
public class CustomerService {

    private static  final  Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList(String keyword){
        String sql = "select * from customer";
        //LOGGER.info(sql);
        return DatabaseHelper.queryEntityListByThreadLocal(Customer.class,sql,null);
    }

    /**
     * 获取客户
     */
    public Customer getCustomer(long id){
        String sql = "select * from customer where id = ?";
        return DatabaseHelper.queryEntityByThreadLocal(Customer.class,sql,id);
    }

    /**
     * 更新客户
     */
    @Transaction
    public void updateCustomer(long id,Map<String,Object> fieldMap){
        DatabaseHelper.updateEntity(Customer.class,id,fieldMap);
    }

    /**
     * 删除客户
     */
    public void deleteCustomer(long id){
        DatabaseHelper.deleteEntity(Customer.class,id);
    }

}
