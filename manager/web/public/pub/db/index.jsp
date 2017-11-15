<%@page import="com.jinhua.User"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	User user=SystemUtils.getSessionUser(request, response);
	if(user==null||!user.getLoginName().equals("admin")){
		%>
		<jsp:forward page="/index.jsp" />
		<%
	}
%>
<!DOCTYPE html>
<html>
 <head>
  <jsp:include page="/public/base.jsp" />
  <script type="text/javascript">
	  var controller = "com.framework.action.Sys_db_operate";
    $(document).ready(function() {
// 	  pageReload();
	
    });
    
    function doExecute(method){
    	var sql=document.getElementById("sql").value;
    	$.ajax({
    		type : "POST",
    		url : "fw?controller=" + controller + "&method="+method,
    		dataType : "json",
    		data : {
    			sql : sql + ""
    		},
    		success : function(data) {
    			var result="当前系统繁忙";try{data = eval('(' + data + ')');	result=data.result;}catch(e){try{data = eval(data);result=data.result;}catch(e1){}}
    			if ("Y" == result) {
    				if(method=='executeSql'){
    					var xx={code:data.count};
        				$('#db_result').datagrid({
        					columns:[[
        					          {field:'code',title:'执行成功',width:100},
        					      ]],
    						data:[xx]
    					});
    				}else{
    					$('#db_result').datagrid({
    						columns:[data.head],
    						data:data.body
    					});
    				}
    			} else {
    				var xx={code:result};
    				$('#db_result').datagrid({
    					columns:[[
    					          {field:'code',title:'执行出错',width:500},
    					      ]],
						data:[xx]
					});
    			}
    		}
    	});
    	
    	
    }
    
    
	
  </script>
 </head>
 <body class="easyui-layout" id="main_layout">
	<div data-options="region:'north',split:true,collapsed:false" style="height:132px;padding: 5px;">
	
		<form class="l-form" id="fd_queryFormObj" method="post">
		    <ul>
		     <li style="width: 100px; text-align: left;">请输入SQL：</li>
	         <li style="width: 620px; text-align: left;height:100px;">
		        <div class="l-text" style="width: 618px;height:95px;">
		          <div>
		          	<textarea id="sql" name="sql" style="width:600px;height:90px;"></textarea>
		          </div>
		          <div class="l-text-l"></div>
		          <div class="l-text-r"></div>
		        </div>
		      </li>
		      <li style="width: 150px; text-align: left;">
				<a href="javascript:void(0)" id="sb" class="easyui-splitbutton"
				        data-options="menu:'#mm',iconCls:'icon-search'" onclick="javascript:doExecute('query');">查询前100行</a>
				<div id="mm" style="width:100px;">
					<div data-options="iconCls:'icon-search'" onclick="javascript:doExecute('queryall');">查询所有</div>
				    <div data-options="iconCls:'icon-set'" onclick="javascript:doExecute('executeSql');">执行</div>
				</div>
			  </li>
		      <li style="width: 5px;"></li>
		     </ul>
	  </form>
	</div>
	<div data-options="region:'center'" style="padding: 5px;">
		<div id="db_result" style="height: 200px;"></div>
	</div>
</body>
</html>
