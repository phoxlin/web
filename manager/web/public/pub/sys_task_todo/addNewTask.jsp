<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.tools.UI_Op"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Entity sys_task_step=(Entity)request.getAttribute("sys_task_step");
	boolean hasSys_task_step=sys_task_step!=null&&sys_task_step.getResultCount()>0;
%>
<!DOCTYPE HTML>
<html>
 <head>
  <jsp:include page="/public/edit_base.jsp" />
  <script type="text/javascript">
    var entity = "sys_task_step";
    var form_id = "sys_task_todo_legendFormObj";
    var lockId=new UUID();
    $(document).ready(function() {

    //insert js

    });
  </script>
  <script type="text/javascript" charset="utf-8" src="public/pub/sys_task_todo/sys_task_todo_legend.js"></script>
 </head>
<body>
	  <form class="l-form" id="sys_task_todo_legendFormObj" method="post">
	    <ul>
	     <li style="width: 170px; text-align: left;">选择任务流程(*)：</li>
	    </ul>
	    <ul>
         <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <%=UI.createSelectByData("taskcode", "creditrecoveryTask:信用恢复", "", true, "width:164px;") %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	  </form>
 </body>
</html>