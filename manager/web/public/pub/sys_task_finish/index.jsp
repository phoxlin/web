<%@page import="com.jinhua.server.task.TaskInfo"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	User user=SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}

	String taskcode = "sys_task_finish";
	String taskname = "已完成任务";
	String sId = request.getParameter("sid");
	
	TaskInfo task=new TaskInfo("sys_task_finish",sId);

//	String xx=task.getLegendFieldValue("legendname", "inputname");
	
%>
<!DOCTYPE html>
<html>
<head>
<title><%=taskname%></title>
<jsp:include page="/public/base.jsp" />
<script type="text/javascript">


//data-grid配置开始
///////////////////////////////////////////(1).sys_task_finish___sys_task_finish_legend开始///////////////////////////////////////////
	//搜索配置
	var sys_task_finish___sys_task_finish_legend_filter=[
				      	 ];
	//编辑页面弹框标题配置
	var sys_task_finish___sys_task_finish_legend_dialog_title='已完成任务';
	//编辑页面弹框宽度配置
	var sys_task_finish___sys_task_finish_legend_dialog_width=700;
	//编辑页面弹框高度配置
	var sys_task_finish___sys_task_finish_legend_dialog_height=300;
	//IndexGrid数据加载提示配置
	var sys_task_finish___sys_task_finish_legend_loading=true;
	//编辑页面弹框宽度配置
	var sys_task_finish___sys_task_finish_legend_entity="sys_task_step";
	//编辑页面路径配置
	var sys_task_finish___sys_task_finish_legend_nextpage="public/pub/sys_task_finish/sys_task_finish_legend_edit.jsp";
	<%
		String sql="select a.*,b.taskname instance_name from sys_task_step a,sys_task_instance b where a.instance_id=b.id and a.state =?";
	
	%>
	
	var sys_task_finish___sys_task_finish_legend_params={
														sql:"<%=sql%>",
														sqlPs:['closed']
													};
	
	
///////////////////////////////////////////(1).sys_task_finish___sys_task_finish_legend结束///////////////////////////////////////////

//data-grid配置结束

</script>
<script type="text/javascript" charset="utf-8" src="public/pub/sys_task_finish/sys_task_finish_legend.js"></script>

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