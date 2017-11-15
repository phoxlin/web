<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	User user=SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}

	String taskcode = "sys_db_record";
	String taskname = "数据库备份记录";
	String sId = request.getParameter("sid");

%>
<!DOCTYPE html>
<html>
<head>
<title><%=taskname%></title>
<jsp:include page="/public/base.jsp" />
<script type="text/javascript">


//data-grid配置开始
///////////////////////////////////////////(1).sys_db_record___sys_db_record开始///////////////////////////////////////////
	//搜索配置
	var sys_db_record___sys_db_record_filter=[
{"rownum":2,"compare":"=","colnum":1,"label":"操作类型","type":"text","columnname":"op_type"},
{"rownum":2,"compare":">=","colnum":2,"label":"操作时间 从","type":"date","columnname":"op_time"},
{"rownum":2,"compare":"<=","colnum":3,"label":"到","type":"date","columnname":"op_time"},
{"rownum":2,"compare":"like","colnum":4,"label":"文件名","type":"text","columnname":"file_name"}
				      	 ];
	//编辑页面弹框标题配置
	var sys_db_record___sys_db_record_dialog_title='数据库备份记录';
	//编辑页面弹框宽度配置
	var sys_db_record___sys_db_record_dialog_width=700;
	//编辑页面弹框高度配置
	var sys_db_record___sys_db_record_dialog_height=500;
	//IndexGrid数据加载提示配置
	var sys_db_record___sys_db_record_loading=true;
	//编辑页面弹框宽度配置
	var sys_db_record___sys_db_record_entity="sys_db_record";
	//编辑页面路径配置
	var sys_db_record___sys_db_record_nextpage="public/pub/db/sys_db_record/sys_db_record_edit.jsp";
///////////////////////////////////////////(1).sys_db_record___sys_db_record结束///////////////////////////////////////////

//data-grid配置结束

</script>
<script type="text/javascript" charset="utf-8" src="public/pub/db/sys_db_record/index.js"></script>
<script type="text/javascript" charset="utf-8" src="public/pub/db/sys_db_record/sys_db_record.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		showTaskView('<%=taskcode%>','<%=sId%>','N');
	});
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/public/menus.jsp" />
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
 						<h1 class="page-header"><%=taskname%></h1>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12">
						<div id="<%=taskcode%>_jh_process_page"> </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>