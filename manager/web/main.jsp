<%@page import="com.gd.m.GdUser"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<div class="">
			<div class="main">
				<div class="menu-container">
					<div class="header">日常应用</div>
					<div class="menus">
						<%
							if (user.getCD().contains("CASHIER")) {
						%>
						<a href="cashier/html/checkin.jsp"> <i class="icon icon-op14"></i> <span>收银台</span>
						</a>
						<%
							}
							if (user.getCD().contains("MEM_INFO")) {
						%>
						<a href="pages/senior_mem/index.jsp"> <i class="icon icon-op1"></i> <span>会员管理</span>
						</a>
						<%
							}
							if (user.getCD().contains("GOODS_INFO")) {
						%>
						<a href="pages/yp_goods/index.jsp"> <i class="icon icon-op3"></i> <span>商品管理</span>
						</a>
						<a href="pages/yp_goods_check/index.jsp"> <i class="icon icon-op3"></i> <span>商品盘点记录</span>
						</a>
						<%
							}
							if (user.getCD().contains("EMP_MANAGE")) {
						%>
						<a href="pages/yp_emp/index.jsp"> <i class="icon icon-op4"></i> <span>员工管理</span>
						</a>
						<%
							}
						%>
					</div>
				</div>


				<div class="menu-container">
					<div class="header">微信与APP</div>
					<div class="menus">
						<%
							if (user.getCD().contains("YP_ARTICLE")) {
						%>
						<a href="pages/yp_article/index.jsp"> <i class="icon icon-mk5"></i> <span>圈圈</span>
						</a>
						<%
							}
						%>
						<%
							if (user.getCD().contains("COURSE_MANAGE")) {
						%>
						<a href="pages/yp_private_plan/index.jsp"> <i class="icon icon-op2"></i> <span>课程管理</span>
						</a>
						<%
							}
						%>

						<!-- <a> <i class="icon icon-mk6"></i> <span>消息推送</span>
						</a> -->

						<%
							if (user.getCD().contains("ACTIVITY_MANAGE")) {
						%>
						<a href="pages/yp_active/index.jsp"> <i class="icon icon-mk1"></i> <span>营销活动</span>
						</a>
						<%
							}
						%>
						<!-- <a> <i class="icon icon-mk7"></i> <span>短信群发</span>
						</a> --> <a> <i class="icon icon-mk8"></i> <span>意见反馈</span>
						</a> <a> <i class="icon icon-mk9"></i> <span>APP用户</span>
						</a>
					</div>
				</div>

				<div class="menu-container">
					<div class="header">财务报表</div>
					<div class="menus">
					<%
							if (user.getCD().contains("YP_STORE_REC_DAY")) {
						%>
						<a href="cashier/greport/day.jsp" target="_blank"> <i
							class="icon icon-rp2"></i> <span>每日报表</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_STORE_REC_MONTH")) {
						%>
						<a href="cashier/greport/month.jsp" target="_blank"> <i
							class="icon icon-rp2"></i> <span>每月报表</span>
						</a>
						<%
							}
							if (user.getCD().contains("FRONT_COLLECT")) {
						%>
						<a href="pages/yp_report/front_collect.jsp"> <i class="icon icon-rp2"></i> <span>收银统计</span>
						</a>
						<%
							}
							if (user.getCD().contains("GOODS_REPORT")) {
						%>
						<a href="pages/yp_report/goods_report.jsp"> <i class="icon icon-rp2"></i> <span>商品统计</span>
						</a>
						<%
							}
							if (user.getCD().contains("PERFORM_TOTAL")) {
						%>
						<!-- 						<a href="pages/yp_report/index.jsp" data-model-code="PERFORM_TOTAL"> <i class="icon icon-rp2"></i> <span>销售统计</span> -->
						<!-- 						</a> -->
						<%
							}
							if (user.getCD().contains("DAY_RANK")) {
						%>
						<!-- 						<a href="pages/yp_report/day_rank.jsp"> <i class="icon icon-rp8"></i> <span>日销售排名</span> -->
						<!-- 						</a> -->
						<%
							}
							if (user.getCD().contains("PERFORM_INFO")) {
						%>
			<!-- 			<a href="pages/yp_gym_perform/index.jsp"> <i class="icon icon-rp9"></i> <span>目标设置</span> 
						</a>-->
						<%
							}
							if (user.getCD().contains("FLOW_MANAGE")) {
						%>
						<a href="pages/yp_flow/index.jsp"> <i class="icon icon-rp10"></i> <span>流水报表</span>
						</a>
						<%
							}
							if (user.getCD().contains("UPDATE_MANAGE")) {
						%>
						<a href="pages/yp_update/index.jsp"> <i class="icon icon-rp12"></i> <span>操作记录表</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_DAY")) {
						%>
						<a href="cashier/report/finance/001.jsp" target="_blank"><i class="icon icon-rp2"></i> <span>销售日报表</span> </a>
						<%
							}
							if (user.getCD().contains("YP_MONTH")) {
						%>
						<a href="cashier/report/finance/002.jsp" target="_blank"> <i class="icon icon-rp2"></i> <span>销售月报表</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_YEAR")) {
						%>
						<a href="cashier/report/finance/003.jsp" target="_blank"> <i class="icon icon-rp2"></i> <span>销售年报表</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_MONTHYEAR")) {
						%>
						<a href="cashier/report/finance/004.jsp" target="_blank"> <i class="icon icon-rp2"></i> <span>销售汇总表</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_GETMONEY")) {
						%>
						<a href="cashier/report/finance/005.jsp" target="_blank"> <i class="icon icon-rp2"></i> <span>收款统计表</span>
						</a>
						<%
							}
						%>
						<%
							if (user.getCD().contains("WAGES_MANAGE")) {
						%>
						<a href="pages/yp_emp_salary/index3.jsp"> <i class="icon icon-rp2"></i> <span>工资管理</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_EMP_PERFORM")) {
						%>
						<a href="pages/yp_report/emp_perform.jsp"> <i class="icon icon-rp2"></i> <span>员工业绩统计</span>
						</a>
						<%
							}
							if (user.getCD().contains("YP_CLASS_HEAT")) {
						%>
						<a href="pages/yp_report/class_heat.jsp"> <i class="icon icon-rp2"></i> <span>课程热度排行榜</span>
						</a>
						<%
					     	}
							if (user.getCD().contains("YP_DAY_REPORT")) {
						%>
<!-- 						<a href="cashier/report/finance/007.jsp"> <i class="icon icon-rp2"></i> <span>收入日报表</span> -->
<!-- 						</a> -->
						<%
							}
 							if (user.getCD().contains("YP_YEAR_MONTH")) {
// 						%> 
<!--  						<a href="cashier/report/finance/006.jsp"> <i class="icon icon-rp2"></i> <span>收入月报表</span> -->
<!-- 						</a> -->
						<%
						}
						%>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>