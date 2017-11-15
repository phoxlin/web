<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.gd.m.GdUser"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.tools.UI_Op"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	GdUser user=(GdUser)SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}
	
	String cust_name = user.getCust_name();
	String good_name = request.getParameter("good_name");
	String good_id = request.getParameter("good_id");
	String gym = request.getParameter("gym");
	String good_no = request.getParameter("good_no");
	Connection conn = null;
	IDB db = new DBM();
	List<Map<String,Object>> goodsList = null;
	Map<String,String> gyms = null;
	try{
		conn = db.getConnection();
		conn.setAutoCommit(true);
		Entity e = new EntityImpl("yp_gym",conn);
		int s = e.executeQuery("select * from yp_gym where cust_name = ?",new Object[]{cust_name});
		if(s>0){
			gyms = new HashMap<String,String>();
			for(int i = 0;i<s;i++){
				gyms.put(e.getStringValue("gym", i), e.getStringValue("gym_name", i));
			}
		}
		Entity goods = new EntityImpl("yp_goods",conn);
		int rs = goods.executeQuery("select gym,price from yp_goods where cust_name = ? and good_no =? group by gym",new Object[]{cust_name,good_no} );
		if(rs>0){
			goodsList = goods.getValues();
		}
	}catch(Exception e){
		
	}finally{
		db.freeConnection(conn);
	}
	
	
%>
<!DOCTYPE HTML>
<html>
 <head>
  <jsp:include page="/public/edit_base.jsp" />
  <script type="text/javascript">
    var entity = "yp_goods_price";
    var form_id = "yp_goods_priceFormObj";
    var lockId=new UUID();
    $(document).ready(function() {
    //insert js

    });
	function updatePrice(p){
		var price_inputs = $("input[name^='price']");
		for(var i = 0;i<price_inputs.length;i++){
			var input = price_inputs[i];
			$(input).val(p);
		}		
	}
  </script>
  <script type="text/javascript" charset="utf-8" src="pages/yp_goods_price/yp_goods_price.js"></script>
 </head>
<body>
	<!-- <h6>说明:如果同一条码的商品放置在多个仓库,只有有一个仓库的该商品价格与提交修改的价格不一致，就会更改所有仓库下的本条码商品</h6> -->
	  <form class="l-form" id="yp_goods_priceFormObj" method="post">
	    <input id="yp_goods_price__id" name="yp_goods_price__id" type="hidden" value=''/>
	    <input id="yp_goods_price__good_id" name="yp_goods_price__good_id" type="hidden" value='<%=good_id==null?"":good_id%>'/>
	    <ul>
	      <li style="width: 120px; text-align: left;">商品名称：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods_price__good_id" name="" class="" readonly="readonly"  style="width: 164px;" type="text" data-options="" value='<%=good_name==null?"没有查询到该商品":good_name%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
		<li style="width: 40px;"></li>
	      <li style="width: 120px; text-align: left;">设置统一价 ：</li>
       <li style="width: 190px; text-align: left;">
	        <div class="l-text" style="width: 188px;">
	          <input id="generic_price" name="generic_price" onkeyup="updatePrice(this.value)" class="" style="width: 164px;" type="number"/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
		<li style="width: 40px;"></li>
	      </ul>
	      
	      <%if(goodsList!=null && goodsList.size()>0){ %>
	      	
	      	<% for(int i = 0;i<goodsList.size();i++){
	      		Map<String, Object> m = goodsList.get(i);
	      	%>
	      		<%if(i%2==0){%> <ul> <%} %>
			      <li style="width: 120px; text-align: left;"><%=gyms.get(m.get("gym")+"") %></li>
		          <li style="width: 170px; text-align: left;">
			        <div class="l-text" style="width: 168px;">
			          <input id="<%=m.get("gym") %>" name="<%="price_"+m.get("gym") %>" class="easyui-validatebox" style="width: 164px;" type="number" data-options="precision:0,required:true" value='<%=Utils.toPriceFromLongStr(m.get("price")+"") %>'/>
			          <div class="l-text-l"></div>
			          <div class="l-text-r"></div>
			        </div>
			      </li>
			      <li style="width: 40px;"></li>
	      		
	      		<%if(i%2==0){%> </ul> <%} %>
	      <%}}%>
	      
	      
	  </form>
 </body>
</html>