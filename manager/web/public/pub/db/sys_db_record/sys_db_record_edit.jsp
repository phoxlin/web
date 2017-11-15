<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.tools.UI_Op"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	User user=SystemUtils.getSessionUser(request, response);
	if(user==null){ request.getRequestDispatcher("/").forward(request, response);}

	Entity sys_db_record=(Entity)request.getAttribute("sys_db_record");
	boolean hasSys_db_record=sys_db_record!=null&&sys_db_record.getResultCount()>0;
%>
<!DOCTYPE HTML>
<html>
 <head>
  <jsp:include page="/public/edit_base.jsp" />
  <script type="text/javascript">
    var entity = "sys_db_record";
    var form_id = "sys_db_recordFormObj";
    var lockId=new UUID();
    $(document).ready(function() {

    //insert js

    });
  </script>
  <script type="text/javascript" charset="utf-8" src="pages/sys_db_record/sys_db_record.js"></script>
 </head>
<body>
	  <form class="l-form" id="sys_db_recordFormObj" method="post">
	    <input id="sys_db_record__id" name="sys_db_record__id" type="hidden" value='<%=hasSys_db_record?sys_db_record.getStringValue("id"):""%>'/>
	    <ul>
	      <li style="width: 90px; text-align: left;">文件名(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	          <input id="sys_db_record__file_name" name="sys_db_record__file_name" class="easyui-validatebox"  style="width: 364px;" type="text" data-options="required:true,validType:'length[0,200]'" value='<%=hasSys_db_record?sys_db_record.getStringValue("file_name"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">压缩(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	          <%=UI.createSelect("sys_db_record__is_zip","PUB_C001",hasSys_db_record?sys_db_record.getStringValue("is_zip"):"",true,"{'style':'width:364px'}") %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">文件大小(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	          <input id="sys_db_record__bak_size" name="sys_db_record__bak_size" class="easyui-validatebox"  style="width: 364px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasSys_db_record?sys_db_record.getStringValue("bak_size"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">哈希码(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	          <input id="sys_db_record__hash_code" name="sys_db_record__hash_code" class="easyui-validatebox"  style="width: 364px;" type="text" data-options="required:true,validType:'length[0,50]'" value='<%=hasSys_db_record?sys_db_record.getStringValue("hash_code"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">文件ID(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	        	<%=UI.createUploadFile("sys_db_record__fk_file_id", hasSys_db_record?sys_db_record.getStringValue("fk_file_id"):"", false, "zip,bak", 1, false, request.getParameter("type")) %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">结果(*)：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;">
	          <input id="sys_db_record__result" name="sys_db_record__result" class="easyui-validatebox"  style="width: 364px;" type="text" data-options="required:true,validType:'length[0,20]'" value='<%=hasSys_db_record?sys_db_record.getStringValue("result"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">原因：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;height: 160px;">
	          <%=UI.createEditor("sys_db_record__cause",hasSys_db_record?sys_db_record.getStringValue("cause"):"",false,new UI_Op("width:99%;height:150px;","")) %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">备注：</li>
       <li style="width: 370px; text-align: left;">
	        <div class="l-text" style="width: 368px;height: 60px;">
	          <textarea id="sys_db_record__remark" name="sys_db_record__remark" class="easyui-validatebox"  style="width: 99%;height: 50px;"  data-options="required:false,validType:'length[0,400]'" ><%=hasSys_db_record?sys_db_record.getStringValue("remark"):""%></textarea>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	  </form>
 </body>
</html>