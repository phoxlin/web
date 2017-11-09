package com.core.smart.Controller;

import com.core.smart.Service.CustomerService;
import com.core.smart.annotation.Action;
import com.core.smart.annotation.Controller;
import com.core.smart.annotation.Inject;
import com.core.smart.http.request.Param;
import com.core.smart.http.response.Data;
import com.core.smart.http.response.View;
import com.core.smart.model.Customer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 客户请求控制
 * Created by Administrator on 2017/11/9.
 */
@Controller
public class CustomerController {

    @Inject
    private CustomerService customerService;

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
}
