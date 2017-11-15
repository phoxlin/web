package com.yepao.manager;


import com.jinhua.server.BasicAction;
import com.jinhua.server.Route;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.m.ContentType;
import com.jinhua.server.m.HttpMethod;
import com.yepao.manager.tools.StringUtil;
/**
 * 运维平台——客户管理
 * @author Administrator
 *
 */
public class CustomerAction extends BasicAction{

	/**
	 * 客户列表
	 */
	@Route(value = "/yp_manager_customer_list", conn = true, mdb = false, m = HttpMethod.GET, type = ContentType.JSON)
	public void deal() throws Exception {
		String cust_name = request.getParameter("cust_name");
		String cust_type = request.getParameter("cust_type");
		String partner = request.getParameter("partner");
		String sale_name = request.getParameter("sale_name");
		String time_from = request.getParameter("time_from");
		String time_to = request.getParameter("time_to");
		String cust_area = request.getParameter("cust_area");
		StringBuffer sql = new StringBuffer("select * from yp_manager_cust where 1=1 ");
		
		if(StringUtil.isNotEmpty(cust_name)){
			sql.append(" and cust_name='").append(cust_name).append("' ");
		}
		if(StringUtil.isNotEmpty(cust_type)){
			sql.append(" and cust_type='").append(cust_type).append("' ");
		}
		if(StringUtil.isNotEmpty(partner)){
			sql.append(" and partner='").append(partner).append("' ");
		}
		if(StringUtil.isNotEmpty(sale_name)){
			sql.append(" and sale_name='").append(sale_name).append("' ");
		}
		if(StringUtil.isNotEmpty(cust_area)){
			sql.append(" and cust_area='").append(cust_area).append("' ");
		}
		if(StringUtil.isNotEmpty(time_from)){
			sql.append(" and sale_name='").append(sale_name).append("' ");
		}
		if(StringUtil.isNotEmpty(time_to)){
			sql.append(" and cust_area='").append(cust_area).append("' ");
		}
		
		Entity en = new EntityImpl(this);
		en.executeQuery(sql.toString());
		
		this.obj.put("data", en.getValues());
	}
	
}
