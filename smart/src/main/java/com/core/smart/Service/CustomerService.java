package com.core.smart.Service;

import com.core.smart.annotation.Service;
import com.core.smart.helper.DatabaseHelper;
import com.core.smart.model.Customer;

import java.util.List;

/**
 * 客户服务类
 * Created by Administrator on 2017/11/9.
 */
@Service
public class CustomerService {

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList(String keyword){
        String sql = "select * from customer";
        return DatabaseHelper.queryEntityListByThreadLocal(Customer.class,sql,null);
    }

}
