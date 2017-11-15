<%@page import="org.json.JSONArray"%>
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


	String good_id = request.getParameter("good_id");
    GdUser user=(GdUser)SystemUtils.getSessionUser(request, response);
    String cust_name = user.getCust_name();
    if(user==null){ request.getRequestDispatcher("/").forward(request, response);}
    Connection conn = null;
    Entity yp_goods = null;
    boolean hasYp_goods = false;
    Entity yp_code = null;
    IDB db = new DBM();
    JSONArray data = new JSONArray();
    try {
		conn = db.getConnection();
		yp_goods = new EntityImpl("yp_goods_check",conn);
		String sql = "SELECT * FROM yp_goods WHERE id=? ";
		yp_goods.executeQuery(sql,new String[]{good_id });
		hasYp_goods=yp_goods!=null&&yp_goods.getResultCount()>0;
		yp_code = new EntityImpl("yp_code", conn);
		int size = yp_code.executeQuery("select * from yp_code where barcode=?  and bartype=? and cust_name=?",
				new String[]{"goods_type", "pub", cust_name});
		if (size > 0) {
			String content = yp_code.getStringValue("content");
			if (content != null && content.length() > 0) {
				data = new JSONArray(content);
			}
		}
		
		
	} catch (Exception e) {
		
	} finally {
		db.freeConnection(conn);
	}
	
%>
<!DOCTYPE HTML>
<html>
<head>
<jsp:include page="/public/edit_base.jsp" />
<script type="text/javascript">
$(document).ready(function() {
	$('#yp_goods__good_type').combobox('loadData',<%=data%>);
	
});
	function save(win){
		var flag = $('#yp_goodsFormObj').form("validate");
		if(flag){
			$.ajax({
				url : "save_gym_goods_check",
				data :
					$('#yp_goodsFormObj').serialize(),  
				
				async : false,
				dataType : "json",
				success : function(data) {
					if (data.rs == 'Y') {
						alert("盘点成功");
						win.close();
					} else {
						error("操作失败,请重试");
					}
				}
			});
	}
	}
</script>
</head>
<body>
	<!-- <h6>说明:如果同一条码的商品放置在多个仓库,只有有一个仓库的该商品价格与提交修改的价格不一致，就会更改所有仓库下的本条码商品</h6> -->
	<form class="l-form" id="yp_goodsFormObj" name="yp_goodsFormObj" method="post">
		<input id="good_id" name="good_id" type="hidden" value='<%=good_id == null ? "" : good_id%>' />
		<input id="goods_check_id" name="goods_check_id" type="hidden" value='<%=hasYp_goods?yp_goods.getStringValue("id"):""%>' />
		<ul>
			<li style="width: 120px; text-align: left;">商品类型：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 468px;">
				  <input id="yp_goods__good_type" name="yp_goods__good_type"
						class="easyui-combobox" style="width: 164px;" type="text"
						data-options="required:true,validType:'length[0,10]',valueField:'id',textField:'text',editable:false"
						value='<%=hasYp_goods ? yp_goods.getStringValue("good_type") : ""%>' />
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		
			<li style="width: 120px; text-align: left;">仓库：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 468px;">
					<%=UI.createSelectBySql("yp_goods_check__store_id","select id code, store_name note from yp_store where gym='" + user.getXX("gym") + "'" ,hasYp_goods?yp_goods.getStringValue("store_id"):"",true,"{'style':'width:164px'}") %>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>
		<ul>
			<li style="width: 120px; text-align: left;">商品名称：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 468px;">
					<input id="yp_goods_check__goods_name" name="yp_goods_check__goods_name" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasYp_goods?yp_goods.getStringValue("good_name"):""%>'/>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
			
			<li style="width: 120px; text-align: left;">商品盘点前数量：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 468px;">
					<input id="yp_goods_check__old_num" name="yp_goods_check__old_num" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:true,min:0" value='<%=hasYp_goods?yp_goods.getStringValue("good_num"):""%>'/>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>
		<ul>
			<li style="width: 120px; text-align: left;">商品盘点后数量：</li>
			<li style="width: 470px; text-align: left;">
				<div class="l-text" style="width: 468px;">
					<input id="yp_goods_check__check_num" name="yp_goods_check__check_num" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:true,min:0" value='<%=hasYp_goods?yp_goods.getStringValue("good_num"):""%>'/>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>
		<ul>
			<li style="width: 120px; text-align: left;">备注：</li>
			<li style="width: 490px; text-align: left;">
				<div class="l-text" style="width: 488px;height:55px;">
				    <textarea id="yp_goods_check__remark" name="yp_goods_check__remark" class="" style="width: 464px;height:50px;"><%=hasYp_goods?yp_goods.getStringValue("remark"):""%></textarea>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>



	</form>
</body>
</html>