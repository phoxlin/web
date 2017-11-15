<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.User"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	User user = SystemUtils.getSessionUser(request, response);
	String userId = user.getId();
	Entity sys_db_bak_set = null;
	Connection conn = null;
	IDB db = new DBM();
	try {
		conn = db.getConnection();
		conn.setAutoCommit(true);
		sys_db_bak_set = new EntityImpl(conn);
		sys_db_bak_set.executeQuery("select * from sys_db_bak_set");
	} catch (Exception e) {

	} finally {
		db.freeConnection(conn);
	}

	boolean hasSys_db_bak_set = sys_db_bak_set != null && sys_db_bak_set.getResultCount() > 0;
%>
<!DOCTYPE HTML>
<html>
<head>
<jsp:include page="/public/base.jsp" />
<script type="text/javascript">
	var controller = "com.framework.action.Sys_db_bak_setAction";
	var entity = "sys_db_bak_set";
	var front_qm_name = "sys_db_bak_set";
	var qm_name = "sys_db_bak_set";
	var form_id = "sys_db_bak_setFormObj";
	var next_page = "public/pub/db/sys_db_bak_set/index.jsp";

	$(document).ready(function() {
	});
	//add others script
	function set() {
		$('#' + form_id).form('submit', {
			url : "task-cq-save",
			onSubmit : function(data) {
				var isValid = $(this).form('validate');
				if (!isValid) {
					$.messager.progress('close');
				}
				var id = $('#sys_db_bak_set__id').val();
				if (id != null && id.length > 5) {
					data.m = "edit";
				} else {
					data.m = "add";
				}

				data.e = entity;
				return isValid;
			},
			success : function(data) {
				$.messager.progress('close');
				var result = "当前系统繁忙";
				try {
					data = eval('(' + data + ')');
					result = data.rs;
				} catch (e) {
					try {
						data = eval(data);
						result = data.rs;
					} catch (e1) {
					}
				}
				if ("Y" == result) {
					info("保存成功")
				} else {
					error("保存失败", result);
				}
			}
		});
	}
</script>
</head>
<body>
	<form class="l-form" id="sys_db_bak_setFormObj" method="post">
		<input id="sys_db_bak_set__id" name="sys_db_bak_set__id" type="hidden" value='<%=hasSys_db_bak_set ? sys_db_bak_set.getStringValue("id") : ""%>' />

		<ul>
			<li style="width: 90px; text-align: left;">是否压缩(*)：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 168px;">
					<%=UI.createSelect("sys_db_bak_set__zip", "PUB_C001", hasSys_db_bak_set ? sys_db_bak_set.getStringValue("zip") : "Y", true, "{'style':'width:164px'}")%>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>
		
		<ul>
			<li style="width: 90px; text-align: left;">备份规则(*)：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 168px;">
					<input id="sys_db_bak_set__rule" name="sys_db_bak_set__rule" class="easyui-validatebox" style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_db_bak_set ? sys_db_bak_set.getStringValue("rule") : "* * * 2 0 0"%>' />
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
		</ul>
		<div style="margin: 20px;color: red;list-style: none;">
		<ul style="list-style-type: none;">
			<li>0 0 12 * * ?  : 每天12点运行</li>
			
			<li>0 15 10 ? * * : 每天10:15运行</li>
			
			<li>0 15 10 * * ? : 每天10:15运行</li>
			
			<li>0 15 10 * * ? * : 每天10:15运行</li>
			
			<li>0 15 10 * * ? 2008 : 在2008年的每天10：15运行</li>
			
			<li>0 * 14 * * ? : 每天14点到15点之间每分钟运行一次，开始于14:00，结束于14:59。</li>
			
			<li>0 0/5 14 * * ? : 每天14点到15点每5分钟运行一次，开始于14:00，结束于14:55。</li>
			
			<li>0 0/5 14,18 * * ? : 每天14点到15点每5分钟运行一次，此外每天18点到19点每5钟也运行一次。</li>
			
			<li>0 0-5 14 * * ? : 每天14:00点到14:05，每分钟运行一次。</li>
			
			<li>0 10,44 14 ? 3 WED : 3月每周三的14:10分到14:44，每分钟运行一次。</li>
			
			<li>0 15 10 ? * MON-FRI : 每周一，二，三，四，五的10:15分运行。</li>
			
			<li>0 15 10 15 * ? : 每月15日10:15分运行。</li>
			
			<li>0 15 10 L * ? : 每月最后一天10:15分运行。</li>
			
			<li>0 15 10 ? * 6L : 每月最后一个星期五10:15分运行。</li>
			
			<li>0 15 10 ? * 6L 2007-2009 : 在2007,2008,2009年每个月的最后一个星期五的10:15分运行。</li>
			
			<li>0 15 10 ? * 6#3 : 每月第三个星期五的10:15分运行。</li>
		</ul>
		</div>
		<ul>
			<li style="width: 90px; text-align: left;">保存方式(*)：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 168px;">
					<%=UI.createSelect("sys_db_bak_set__save_type", "PUB_C203", hasSys_db_bak_set ? sys_db_bak_set.getStringValue("save_type") : "001", true, "{'style':'width:164px'}")%>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 500px;"><p style="color: red;">
					提示：<br /> 1：全部保存：表示所有备份的文件都将保存在系统里面；<br /> 2：保存文件个数：表示系统将只保存一定数量（小于等于0时不处理）的备份文件，超过的文件将删除最早的文件；<br /> 3：保存天数：表示系统只保存一定天数(小于等于0时不处理)的备份文件，超过天数的文件将从最早的时间删除；<br /> 4：保存文件总大小：表示系统备份文件的总大小不得超过设置值，如果超过将删除最早的备份文件。
				</p></li>
		</ul>
		<ul>
			<li style="width: 90px; text-align: left;">保存数量(*)：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 168px;">
					<input id="sys_db_bak_set__save_val" name="sys_db_bak_set__save_val" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:true" value='<%=hasSys_db_bak_set ? sys_db_bak_set.getStringValue("save_val") : "0"%>' />
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>

		<ul>
			<li style="width: 90px; text-align: left;">备注：</li>
			<li style="width: 170px; text-align: left;">
				<div class="l-text" style="width: 168px; height: 50px;">
					<textarea id="sys_db_bak_set__remark" name="sys_db_bak_set__remark" class="easyui-validatebox" style="width: 99%; height: 40px;" data-options="required:false,validType:'length[0,400]'"><%=hasSys_db_bak_set ? sys_db_bak_set.getStringValue("remark") : ""%></textarea>
					<div class="l-text-l"></div>
					<div class="l-text-r"></div>
				</div>
			</li>
			<li style="width: 40px;"></li>
		</ul>
	</form>
	<%
		if (hasSys_db_bak_set && sys_db_bak_set.getStringValue("id") != null && sys_db_bak_set.getStringValue("id").length() > 0) {
	%>
	<a id="btn" href="javascript:set();" class="easyui-linkbutton" data-options="iconCls:'icon-save'">更新</a>
	<%
		} else {
	%>
	<a id="btn" href="javascript:set();" class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>
	<%
		}
	%>



</body>
</html>
