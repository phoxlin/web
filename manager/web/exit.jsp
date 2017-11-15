<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String t=request.getParameter("t");
	String index="index.jsp";
	String cust_name=request.getParameter("c");
	if("pc".equalsIgnoreCase(t)){
		index="index.jsp";
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>退出系统</title>
</head>
<body>
	<div id="flag">系统退出。。。</div>
	<div id="forward">正在准备跳转页面。。。(<span id="time">2</span>秒)</div>
	<script type="text/javascript">
		function doExit(){
			setTimeout(function(){
				var time=document.getElementById('time');
				var timeStr=time.innerHTML;
				var timeNum=parseInt(timeStr);
				if(timeNum--<=1){
					location.href="exit?index=<%=index%>&cust_name=<%=cust_name%>";
				}else{
					time.innerHTML=timeNum;
					doExit();
				}
			},1000);
		}
		doExit();
	</script>

</body>

</html>
