<%@page import="java.io.File"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String cust_name = request.getParameter("cust_name");
//cust_name="gdfl";
	String baseUrl = Utils.getWebRootPath();
	String logoUrl = "public/images/"+cust_name+"/logo.png";
	File logo = new File(baseUrl + logoUrl);
	if (!logo.exists()) {
		logoUrl = "public/images/yepao/logo.png";
	}
%>
<!DOCTYPE html>
<html lang="en" style="width: 100%; height: 100%;">
<head>
    <title>登录</title>
	<jsp:include page="public/base.jsp"></jsp:include>  
	<link rel="stylesheet" type="text/css" href="public/css/login.css">
</head>
<body onkeydown="keyDown(event);" style="width: 100%; height: 100%;">
	<div class="bg">
		<div class="login_bg">
			<div class="header">
				<span class="logo">
					<img src="<%=logoUrl%>"/>
				</span>
			</div>
			<div class="c">
				<p class="font20">后台管理系统</p>
				<div class="input-container" style="margin-top: 30px;">
					<span> <i class="user"></i></span> 
					<input placeholder="管理员账号/员工手机号" id="userName" name="userName" type="text" autofocus>
				</div>
				<div class="input-container" style="margin-top: 15px;">
					<span> <i class="pwd"></i> </span> 
					<input placeholder="密码" id="password" name="password" type="password" value="">
				</div>
				<a href="javascript:login();" class="login-btn active">登&nbsp;&nbsp;录</a>
			</div>
		</div>
		<div style="position: absolute;top:98%;left:40%;"><span style="color:white">©2016 苏州金菲科智能科技有限公司 版权所有</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.miitbeian.gov.cn" target="_blank" style="color:white">苏ICP备17026697号-1</a></div>
	</div>
        
     <script type="text/javascript">
     	function keyDown(e){
     		if(13 == e.keyCode){
     			login();
     		}
     	}
     	
    	function login(){
    		var name=$('#userName').val();
    		var pwd=$('#password').val();
    		var at = store.get('jh_access_token');
    		$.ajax({
    			type : 'POST',
    			url : 'ws-login-backend',
    			data:{
    				name:name,
    				pwd:pwd,
    				jh_access_token:at,
    				cust_name: '<%=cust_name%>'
    			},
    			dataType : 'json',
    			success : function(data) {
    				var result = "当前系统繁忙";
    				result = data.rs;
    				if (result == 'Y') {
    					logined=true;
   						location.href = "main.jsp";
    				} else {
    					logined=false;
   						alert(result);
    				}
    			},
    			error : function(xhr, type) {
    				logined=false;
    				alert('系统请求出错');
    			}
    		});    	
    	}
    </script> 
    <script type="text/javascript" src="public/js/bootstrap.min.js"></script>
</body>

</html>
    