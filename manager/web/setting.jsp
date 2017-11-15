<%@page import="com.gd.m.GdUser"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	GdUser user = (GdUser) SystemUtils.getSessionUser(request, response);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>后台系统</title>
<jsp:include page="/public/base.jsp" />
</head>
<body>
	<div class="widget main-bg">
		<jsp:include page="/public/header.jsp">
			<jsp:param value="true" name="isMain"/>
		</jsp:include>
		<div class="main">
			<div class="menu-container">
				<div class="header">
					系统设置
				</div>
				<div class="menus">
					<%
					if(user.getCD().contains("CARD_INFO")){
					%> 
					<a href="pages/yp_type/index.jsp">
						<i class="icon icon-op5"></i> <span>卡种管理</span>
					</a>
					<%

						}
						if(user.getCD().contains("BOX_MANAGE")){
					%> 
					<a href="pages/yp_box/index.jsp" >
						<i class="icon icon-op6"></i> <span>箱柜管理</span>
					</a>
					<%

						}
                       if(user.getCD().contains("PLACE_MANAGE")){
					%>
					<a href="pages/yp_place/index.jsp">
						<i class="icon icon-sys5"></i>
						<span>场地设置</span>
					</a>
					<%
					} 
					%>
					<a href="javascript: alert('功能开发中，敬请期待');" style="display: none;">
						<i class="icon icon-sys6"></i>
						<span>提醒设置</span>
					</a>
					<a href="javascript: alert('功能开发中，敬请期待');" style="display: none;">
						<i class="icon icon-sys7"></i>
						<span>功能开关</span>
					</a>
					<a href="javascript: alert('功能开发中，敬请期待');" style="display: none;">
						<i class="icon icon-sys9"></i>
						<span>积分设置</span>
					</a>
					<%
					 if(user.getCD().contains("YP_DEAL")){
					%>
					<a href="pages/yp_deal/index.jsp">
						<i class="icon icon-sys10"></i>
						<span>协议设置</span>
					</a>
					<%} %>
					<%
							if(user.getCD().contains("TCLASS_PLAN")){
						%> 
						<a href="pages/yp_class_plan/index.jsp"> <i class="icon icon-op12"></i>
							<span>课程安排</span>
						</a> 
						<%} %>
					<!-- <a href="javascript: alert('功能开发中，敬请期待');">
						<i class="icon icon-sys11"></i>
						<span>折扣设置</span>
					</a> -->
					
					<%if(user.getCD().contains("YP_COUNTER_SET")){
					%>
					<!-- <a href="pages/yp_counterFee/counterFee.jsp">
						<i class="icon icon-sys13"></i>
						<span>手续费设置</span>
					</a> -->
					<%
					} if(user.getCD().contains("YP_SET")){
					%>
					<a href="pages/yp_set/yp_set.jsp">
						<i class="icon icon-sys14"></i>
						<span>基础设置</span>
					</a>
					<%
					} if(user.getCD().contains("CLASSLEAVE_SET")){
					%>
					<a href="pages/yp_ordersetting/yp_ordersetting.jsp">
						<i class="icon icon-sys15"></i>
						<span>预约设置</span>
					</a>
					<%
					} if(user.getCD().contains("YP_DEVICE")){
					%>
					<a href="pages/yp_device/index.jsp">
						<i class="icon icon-sys17"></i>
						<span>会所资产</span>
					</a>
					<%
					} if(user.getCD().contains("YP_DEVICE_USER")){
					%>
					<!-- <a href="pages/yp_device_user/index.jsp">
						<i class="icon icon-sys18"></i>
						<span>会所资产分配</span>
					</a> -->
					<%
					} if(user.getCD().contains("YP_CONTRACT")){
					%>
					<a href="pages/yp_contract/index.jsp">
						<i class="icon icon-sys19"></i>
						<span>合同管理</span>
					</a>
					<%
					} if(user.getCD().contains("YP_IMPORT")){
					%>
					<a href="pages/yp_import/index.jsp">
						<i class="icon icon-sys20"></i>
						<span>Excel数据导入</span>
					</a>
					<%
					} if(user.getCD().contains("YP_MODEL")){
					%>
<!-- 						<a href="pages/yp_model/index.jsp"> -->
<!-- 							<i class="icon icon-sys21"></i> -->
<!-- 							<span>系统模块</span> -->
<!-- 						</a> -->
					<%
					} if(user.getCD().contains("YP_CODE")){
					%>
					<!-- <a href="pages/yp_code/index.jsp">
						<i class="icon icon-sys22"></i>
						<span>配置码表</span>
					</a> -->
					<%
					} if(user.getCD().contains("YP_DISABLE_PERIOD")){
					%>
					<a href="pages/yp_disable_period/index.jsp">
						<i class="icon icon-sys23"></i>
						<span>禁用时间段</span>
					</a>
					<%
					} if(user.getCD().contains("YP_PAY_SET")){
					%>
					<!-- <a href="pages/pay/payParsSet.jsp">
						<i class="icon icon-sys24"></i>
						<span>支付设置</span>
					</a> -->
					<%
					}
					%>
					<%
							if(user.getCD().contains("ROYALTY_MANAGE")){
						%> 
					    <a href="pages/yp_emp_commission/index.jsp"> <i class="icon icon-op15"></i>
							<span>提成管理</span>
						</a> 
					<%} %>
					
					<a href="pages/yp_import/logo.jsp"> 
						<i class="icon icon-sys14"></i>
						<span>LOGO上传</span>
					</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>