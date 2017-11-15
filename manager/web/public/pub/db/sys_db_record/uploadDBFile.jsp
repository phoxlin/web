<%@page import="com.jinhua.server.tools.UI"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
%>

<!DOCTYPE HTML>
<html>
<head>
<jsp:include page="/public/base.jsp" />
<script type="text/javascript">
	var controller = "com.framework.action.Sys_db_bak_recordAction";
	var entity = "sys_db_record";
	var qm_name = "sys_db_bak_record";

	var form_id = "uploadFormObj";
	
	function saveDBFile(obj,doc){
		confirm("你确定要用上传的备份文件恢复数据库吗？警告：此操作有可能造成数据丢失！确定执行吗？？", "doSaveDBFile()");
	}
	function doSaveDBFile(){
		$.messager.progress(); 
		$('#' + form_id).form('submit',	{
			url : "db-getback-byfile",
			onSubmit : function(data) {
				var isValid = $(this).form('validate');
				if (!isValid) {
					$.messager.progress('close');
				}
				return isValid;
			},
			success : function(data) {
				$.messager.progress('close');
				var result="当前系统繁忙";try{data = eval('(' + data + ')');	result=data.rs;}catch(e){try{data = eval(data);result=data.rs;}catch(e1){}}
				if ("Y" == result) {
					info("操作成功");
				} else {
					error("保存失败", result);
				}
			}
		});
	}
	
	
</script>
</head>
<body>
	  <form class="l-form" id="uploadFormObj" method="post">
	    <ul>
	      <li style="width: 120px; text-align: left;">数据库建模文件(*)：</li>
	      <li style="width: 290px; text-align: left;">
	        <div class="l-text" style="width: 288px;">
	          <%=UI.createUploadFile("upload_db_file", "", true, "bak,zip", 1, false,request.getParameter("type")) %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul style="color: red;">
	    	<li>请选择数据库备份文件进行恢复操作.支持格式为:bak,zip</li>
	    </ul>
	  </form>
  	</body>
  </html>