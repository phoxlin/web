<%@page import="com.jinhua.server.tools.Resources"%>
<%@page import="com.jinhua.server.upload.QiniuAction"%>
<%@page import="java.io.File"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.gd.m.GdUser"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	GdUser user = (GdUser) SystemUtils.getSessionUser(request, response);

	String cust_name = request.getParameter("cust_name");
	if(cust_name==null||cust_name.length()<=0){
		if (user != null) {
			cust_name = user.getXX("cust_name");
		}else{
			cust_name="yepao";
		}
	}
%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">


<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">


<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/bootstrap/dist/css/bootstrap.min.css">
<!-- MetisMenu CSS -->
<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/metisMenu/dist/metisMenu.min.css">
<!-- Custom CSS -->
<link rel="stylesheet" type="text/css" href="public/sb_admin2/dist/css/sb-admin-2.css">
<!-- Custom Fonts -->
<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/font-awesome/css/font-awesome.min.css">

<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/easyui-1.4.4/themes/bootstrap/easyui.css">
<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/easyui-1.4.4/themes/icon.css">

<link rel="stylesheet" type="text/css" href="public/css/bootstrap-table.css">
<link rel="stylesheet" type="text/css" href="public/css/bootstrap-datetimepicker.min.css">
<link rel="stylesheet" type="text/css" href="public/css/bootstrap-datepicker3.min.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<!-- jQuery -->
<script src="public/sb_admin2/bower_components/jquery/dist/jquery.min.js"></script>

<!-- Bootstrap Core JavaScript -->
<script type="text/javascript" src="public/sb_admin2/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- Metis Menu Plugin JavaScript -->
<script type="text/javascript" src="public/sb_admin2/bower_components/metisMenu/dist/metisMenu.min.js"></script>
<!-- Custom Theme JavaScript -->

<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/jquery.easyui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/myValidate.js"></script>

<script type="text/javascript" charset="utf-8" src="public/js/artDialog.js?skin=default"></script>
<script type="text/javascript" charset="utf-8" src="public/js/iframeTools.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/artDialog.notice.source.js"></script>

<script type="text/javascript" charset="utf-8" src="public/sb_admin2/dist/js/sb-admin-2.js"></script>

<script type="text/javascript" charset="utf-8" src="public/js/bootstrap-table.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/bootstrap-table-toolbar.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/bootstrap-table-zh-CN.min.js"></script>


<script type="text/javascript" charset="utf-8" src="public/js/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/bootstrap/js/bootstrap-select.min.js"></script>

<script type="text/javascript" charset="utf-8" src="public/js/json2.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/template.js"></script>

<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/jquery/dist/jquery.cookie.js"></script>


<script type="text/javascript">
<!--
	template.config({
		sTag : '<#', eTag: '#>'
	});
//-->
	var contextPath='<%=request.getContextPath()%>';
</script>

<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/moxie.min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/plupload.dev.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/i18n/zh_CN.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/ui.js"></script>
<%
 	if(Resources.getProperty("yun.filestore.type", "local").equals("qiniu")){
 		if(QiniuAction.configV1){
%>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/qiniuV1.js"></script>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/main.js"></script>
<%
 		}else{
%>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/qiniu.js"></script>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/main.js"></script>
<%
 		}
 	}else{
%>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/qiniu_local.js"></script>
	<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/main_local.js"></script>
<%
 	}
%>

<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/qiniu/highlight.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/jinhua-yun-1.0.0.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/kindeditor/kindeditor-all-min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/designer/files/task-flat-ui.js"></script>
<script type="text/javascript" charset="utf-8" src="public/ms/common_query.js"></script>
<script type="text/javascript" charset="utf-8" src="public/designer/common_query.task.op.js"></script>


<!-- 主体样式 -->
<link rel="stylesheet" type="text/css" href="public/css/page.css">
<link rel="stylesheet" type="text/css" href="public/css/main.css">
<link rel="stylesheet" type="text/css" href="public/css/icon.css">
<link rel="stylesheet" type="text/css" href="public/css/header.css">

<script type="text/javascript" charset="utf-8" src="public/js/public.js"></script>

<!-- 搜索框查询 -->
<script type="text/javascript" src="public/js/areaGymSelect.js"></script>
<!-- 搜索框查询 -->


<jsp:include page="/public/designer/tpl/taskTpl-Content.jsp"></jsp:include>
<jsp:include page="/public/designer/tpl/taskTpl-UI.jsp"></jsp:include>
<jsp:include page="/public/designer/tpl/taskTpl-Forward.jsp"></jsp:include>
<jsp:include page="/public/ms/tpl/default/common_query_index.jsp"></jsp:include>




