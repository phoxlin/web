package com.core.smart.controller;

import com.core.smart.service.CustomerService;
import com.core.smart.annotation.Action;
import com.core.smart.annotation.Controller;
import com.core.smart.annotation.Inject;
import com.core.smart.http.request.FileParam;
import com.core.smart.http.request.Param;
import com.core.smart.http.response.Data;
import com.core.smart.http.response.View;
import com.core.smart.model.Customer;
import java.util.List;
import java.util.Map;

/**
 * 客户请求控制
 * Created by Administrator on 2017/11/9.
 */
@Controller
public class CustomerController {

    @Inject
    private CustomerService customerService;

    /**
     * 参数优化后（可以不用写Param）
     */
    @Action("get:/customer")
    public View getCustomerList(Param param){
        List<Customer> customers = customerService.getCustomerList(null);
        View view = new View("customer/customer.jsp");
        view.addModel("customers",customers);

        return view;
    }

    @Action("get:/getCustomer")
    public Data queryEntity(Param param)throws Exception{
        Long id = param.getLong("id");
        Customer customer = customerService.getCustomer(id);
        return new Data(customer);
    }

    @Action("get:/updateCustomer")
    public View update(Param param){
        Long id = param.getLong("id");
        customerService.updateCustomer(id,param.getMap());


        List<Customer> customers = customerService.getCustomerList(null);
        View view = new View("customer/customer.jsp");
        view.addModel("customers",customers);
        return view;
    }

    /**
     * 处理 创建客户 请求
     * @param param
     * @return
     */
    @Action("post:/customer_create")
    public Data createSubmit(Param param){
        Map<String,Object> fieldMap = param.getFieldMap();
        FileParam fileParam = param.getFile("photo");
        boolean result = customerService.createCustomer(fieldMap,fileParam);
        return new Data(result);
    }

}
