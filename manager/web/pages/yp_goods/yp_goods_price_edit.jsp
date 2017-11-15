<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.tools.UI_Op"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	User user=SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}

	Entity yp_goods_price=(Entity)request.getAttribute("yp_goods_price");
	boolean hasYp_goods_price=yp_goods_price!=null&&yp_goods_price.getResultCount()>0;
	
	String good_name = request.getParameter("good_name");
	String good_id = request.getParameter("good_id");
	String gym = request.getParameter("gym");
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
	$.ajax({
		url:"show_goods_pirce_by_goodid",
		data:{good_id:"<%=good_id==null?"":good_id%>",gym:"<%=gym==null?"":gym%>"},
		asnyc:false,
		dataType:"json",
		success:function(data){
			if(data.rs == "Y"){
				$("#yp_goods_price__id").val(data.id);
				$("#yp_goods_price__new_price").val(data.new_price);
			}
		}
	});
    //insert js

    });
  </script>
  <script type="text/javascript" charset="utf-8" src="pages/yp_goods_price/yp_goods_price.js"></script>
 </head>
<body>
	  <form class="l-form" id="yp_goods_priceFormObj" method="post">
	    <input id="yp_goods_price__id" name="yp_goods_price__id" type="hidden" value=''/>
	    <input id="yp_goods_price__good_id" name="yp_goods_price__good_id" type="hidden" value='<%=good_id==null?"":good_id%>'/>
	    <ul>
	      <li style="width: 90px; text-align: left;">商品名称：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods_price__good_id" name="" class="" readonly="readonly"  style="width: 164px;" type="text" data-options="" value='<%=good_name==null?"没有查询到该商品":good_name%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
		<li style="width: 40px;"></li>
	      </ul>

		<ul>	      
	      <li style="width: 90px; text-align: left;">调整后价格(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods_price__new_price" name="yp_goods_price__new_price" class="easyui-validatebox" style="width: 164px;" type="number" data-options="precision:0,required:true" value=''/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	  </form>
 </body>
</html>