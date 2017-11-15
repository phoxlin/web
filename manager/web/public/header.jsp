<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="java.io.File"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="com.gd.m.GdUser"%>
<%@page import="com.gd.app.utils.AppUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String view = request.getParameter("view");
	GdUser user=(GdUser)SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}
	String user_name = user.getXX("emp_name");
	String cust_name = user.getXX("cust_name");
	if(user_name == null){
		user_name = user.getXX("login_name");
	}
	String gym = user.getGym();
	String gname = AppUtils.getGymName(gym);
	String baseUrl = Utils.getWebRootPath();
	String logo_url = "public/images/main/logo.png";
	if(cust_name != null && cust_name.length() > 0){
		logo_url = "public/images/"+cust_name+"/logo_main.png";
		File file = new File(baseUrl + logo_url);
		if(!file.exists()){
			logo_url = "public/images/yepao/logo_main.png";
		}
	}
	
	String main = request.getParameter("isMain"); //是否是主页头部
	String _class = "nav-bg";
	if(main != null && main.length() > 0 && "true".equals(main)){
		_class = "";
	}
%>

<div class="nav nav1 <%=_class%>">
	<div class="navbar-header" style="cursor: pointer;" onclick="home()">
		<%
			if("lr".equals(cust_name)){
		%>
			<img src="<%=logo_url%>" style="width: 215px;margin-bottom: 10px;margin-left: 30px;">
		<%		
			} else {
		%>
			<img src="<%=logo_url%>" style="max-height: 80px;margin-left: 30px;">
		<%
			}
		%>
		
		<%
			if(view != null && view.length() > 0){
		%>
			<label style="font-size: 15px;">&nbsp;&nbsp;|&nbsp;&nbsp;<%=view %></label>
		<%
			}
		%>
	</div>
	<ul class="navbar-right">
		<li class="icon-msg">
			消息
			<span class="msg">0</span>
		</li>
		<li class="icon-setting" onclick="setting()">设置</li>
		<li class="me-info" onclick="showMe()">
			<img src="public/images/main/icon_head.png" style="width: 40px; margin-right: 10px;">
			<span><%=user_name %> <span style="font-size: 13px;">(<%=gname %>)</span> </span>
			<a class="icon-more"></a>
			
			<dl class="hover-menu">
				<dt class="menu-info">
					<i class="menu-arrow"></i>
					<i class="menu-layer"></i>
					<span class="menu-header">
						<span class="user-photo-box">
							<i class="user-photo" style="background-image:url(public/images/main/icon_head.png);filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='public/images/main/icon_head.png', sizingMethod='scale');"></i>
						</span>
						<span class="user-info">
							<span class="username"><%=user_name %></span>
						</span>
					</span>
				</dt>
				<dd class="desc-box">
					<ul class="desc-link">
						<li class="link-item">
							<a href="javascript:alert('功能开发中，敬请期待');">个人资料</a>
						</li>
						<li class="link-item">
							<a href="javascript:alert('功能开发中，敬请期待');">帮助中心</a>
						</li>
						<li class="link-item">
							<a href="javascript:updatePwd();">修改密码</a>
						</li>
						<li class="link-item">
							<a href="javascript:exit();">退出</a>
						</li>
					</ul>
				</dd>
			</dl>
		</li>
	</ul>
</div>


<script type="text/javascript">
$(function(){
	var margin = $(".main").css("margin-top");
	margin = margin.replace(/[^0-9]/ig,""); 
	var height = $(window).height() - margin - 20;
	$(".main2").height(height);
	
	window.onresize = function(){
//			var height = $(window).height() - $(".nav1").height();
//			if(height < 700){
//				height = 700;
//			}
//			$(".widget").height(height);
	}
	
	var p=0;
	s();
	$(window).scroll(function () {
		s();
	});
});
function exit() {
	art.dialog({
		title : '确认',
		content : '您确定要退出系统吗？',
		icon : 'question',
		lock : true,
		okVal : "确定",
		ok : function() {
			location.href = "exit.jsp?c=<%=cust_name%>";
		},
		cancelVal : "取消",
		cancel : function() {
			return true;
		}
	});
}

function showMe(){
	$(".hover-menu").show();
}

function home(){
	location.href="main.jsp";
}
function setting(){
	location.href="setting.jsp";
	
}

function s(){
	p = $(this).scrollTop();
	if(p >= 45){
		$(".nav").addClass("cover");
	} else if(p < 45){
		$(".nav").removeClass("cover");
	}
}

//修改密码
function updatePwd(){
	art.dialog.open("pages/yp_emp/emp_updatePwd_self.jsp?cust_name=<%=cust_name%>", {
		title : '修改密码',
		lock : true,
		width : 450,
		height: 200,
		okVal : "修改",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			iframe.updateDialog(this, document, "yp_emp");
			return false;
		},
		cancelVal : "关闭",
		cancel : function() {
			return true;
		}
	});
}
</script>