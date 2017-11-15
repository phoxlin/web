<%@page import="org.json.JSONObject"%>
<%@page import="com.gd.m.Shop"%>
<%@page import="java.util.List"%>
<%@page import="com.gd.m.Cust"%>
<%@page import="com.gd.m.GdUser"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	GdUser user = (GdUser) SystemUtils.getSessionUser(request, response);
	if (user == null) {
		request.getRequestDispatcher("/").forward(request, response);
	}
	Cust cust = user.getCust();
	List<Shop> shops = cust.getShops();
	JSONArray gymArr = new JSONArray();
	if (shops.size() > 0) {
		for (Shop shop : shops) {
			JSONObject obj = new JSONObject();
			obj.put("areacode", shop.getArea_code());
			obj.put("gym", shop.getGym());
			obj.put("gymName", shop.getGym_name());
			gymArr.put(obj);
		}
	}

	String c = request.getParameter("c");//码表代码
	String n = request.getParameter("n"); //码表中文名
	String t = request.getParameter("t"); //码表类型
	String g = request.getParameter("g"); //客户代码

	//查询码表信息
	Entity yp_code = null;
	Connection conn = null;
	IDB db = new DBM();
	JSONArray data = new JSONArray();
	String id = "";
	try {
		conn = db.getConnection();
		conn.setAutoCommit(true);
		yp_code = new EntityImpl("yp_code", conn);
		int size = yp_code.executeQuery(
				"select id,content from yp_code where barcode=? and note=? and bartype=? and cust_name=?",
				new String[] { c, n, t, g });
		if (size > 0) {
			String content = yp_code.getStringValue("content");
			if (content != null && content.length() > 0) {
				data = new JSONArray(content);
			}
			id = yp_code.getStringValue("id");
		} else {
			yp_code.setValue("barcode", c);
			yp_code.setValue("note", n);
			yp_code.setValue("bartype", t);
			yp_code.setValue("cust_name", g);
			yp_code.setValue("remark", n);
			id = yp_code.create();
		}
	} catch (Exception e) {

	} finally {
		db.freeConnection(conn);
	}
%>
<!DOCTYPE html>
<html>
<head>
<title>客户码表</title>
<style>
#tree>li>.tree-node {
	height: 26px;
	padding-top: 5px;
}

#tree>li .tree-title {
	font-size: 14px;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		//初始化码表tree
		$('#tree').tree({
			data :<%=data%>
	});

		//点击码表
		$('#tree').tree({
			onSelect : function(node) {
				showGymList(node.id);
			}
		});
	});
	function showGymList(areacode){
		var arr = <%=gymArr%>;
		 var xx=[];
		for(var i = 0;i<arr.length;i++){
			var d = arr[i];
			if(areacode == d.areacode){
				var json={"text":d.gymName,"id":d.gym};
				xx.push(json);
			}
		 }
		 $('#gym_tree').tree({
				data :xx
		 });
	}
</script>

<script type="text/javascript" charset="utf-8"
	src="public/pub/code/index.js"></script>

</head>
<body>
	<div class="row left-main" style="margin: 0; padding: 20px 0;">
		<div class="col-md-12 col-sm-12">
			<ul id="tree" style="margin-top: 20px; border: 1px solid #ccc;"></ul>
		</div>
		<div class="col-md-12 col-sm-12">
			<ul id="gym_tree" style="margin-top: 20px; border: 1px solid #ccc;"></ul>
		</div>
	</div>
</body>
</html>