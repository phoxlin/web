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
	    <input id="sys_task_step__id" name="sys_task_step__id" type="hidden" value='<%=hasSys_task_step?sys_task_step.getStringValue("id"):""%>'/>
	    <input id="sys_task_step__instance_id" name="sys_task_step__instance_id" type="hidden" value='<%=hasSys_task_step?sys_task_step.getStringValue("instance_id"):""%>'/>
	    <ul>
	      <li style="width: 120px; text-align: left;">实例编号(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__instance_no" name="sys_task_step__instance_no" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("instance_no"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 120px; text-align: left;">当前流程代码(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__taskcode" name="sys_task_step__taskcode" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("taskcode"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 120px; text-align: left;">当前流程(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__taskname" name="sys_task_step__taskname" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("taskname"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 120px; text-align: left;">上一步代码：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__prev_taskcode" name="sys_task_step__prev_taskcode" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:false,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("prev_taskcode"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 120px; text-align: left;">下一步代码：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__next_taskcode" name="sys_task_step__next_taskcode" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:false,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("next_taskcode"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 120px; text-align: left;">操作人(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__userid" name="sys_task_step__userid" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,32]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("userid"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 120px; text-align: left;">操作时间(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__op_time" name="sys_task_step__op_time" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("op_time"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 120px; text-align: left;">状态(*)：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="sys_task_step__state" name="sys_task_step__state" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,20]'" value='<%=hasSys_task_step?sys_task_step.getStringValue("state"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	  </form>
 </body>
</html>