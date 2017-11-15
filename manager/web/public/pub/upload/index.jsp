<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String sId=UUID.randomUUID().toString();
%>
<!DOCTYPE HTML>
<html>
<head>
<jsp:include page="/public/base.jsp" />
<link href="public/sb_admin2/bower_components/pure/pure.css" rel="stylesheet" type="text/css" />
<script src="public/sb_admin2/bower_components/pure/pure.js"></script>

<script type="text/javascript">
		var fileNumber="<%=request.getParameter("number")%>";
		var controller = "com.framework.action.UploadFileAction";
		var entity = "sys_file";
		var buttonImg="upload.png";
		var id="<%=request.getParameter("id")%>";
		var extDesc=new Array();
		var exts="<%=request.getParameter("ext")%>";
		if(exts!=null&&exts.length>0){
			var extList=exts.split(",");
			for(var i=0;i<extList.length;i++){
				var temp=extList[i];
				extDesc.push("*."+temp);
			}
		}else{
			extDesc.push("*.*");
		}
		
		var pic="<%=request.getParameter("pic")%>";
		if("Y"==pic){
			buttonImg="uploadPic.png";
		}
		
		
		
		function parseFile(num){
			//QM_dept_1_b_td_div_dept_name
			var filesize=document.getElementById("QM_sys_file_"+num+"_b_td_div_file_size").innerHTML;
			filesize=formatFileSize(filesize);
			document.getElementById("QM_sys_file_"+num+"_b_td_div_file_size").innerHTML=filesize;
		}
		
		function addFile(obj,doc){
			
			var inp=doc.getElementById(id);
			
			var ids=getAllValuesByName("sys_file","id");
			if(ids.length>0){
				var names=getAllValuesByName("sys_file","re_name");
				
				var inpVal=inp.value;
				
				//检查是否上传文件数量超过了。
				var vals=inpVal.split(',');
				if(inpVal==null||inpVal.length<=0){
					vals=new Array();
				}
				
				var tempLength=vals.length+ids.length;
				if(tempLength>fileNumber){
					//超过了
					var aTags=doc.getElementsByName(id+"_a_tag");
					var imgTags=doc.getElementsByName(id+"_img_tag");
					var indexI=0;
					for(var m=0;m<(tempLength-fileNumber);m++){
						indexI=m;
						var tempA=aTags[0];
						var tempImg=imgTags[0];
						try{
							tempA.parentNode.removeChild(tempA);
							tempImg.parentNode.removeChild(tempImg);
						}catch(e){
						}
					}
					var tempVals=new Array();
					for(indexI=indexI+1;indexI<vals.length;indexI++){
						tempVals.push(vals[indexI]);
					}
					inpVal=tempVals.join(',');
				}
				
				//设置新上传的文件信息
				var desc=doc.getElementById("desc_"+id);
				var html=desc.innerHTML;
				
				for(var i=0;i<ids.length;i++){
					if(i!=0){
						html+=" ";
						
					}
					if(inpVal.length>0){
						inpVal+=",";
					}
					inpVal+=ids[i];
					html+=" <a id='"+ids[i]+"_1' name='"+id+"_a_tag' href='public/pub/upload/down.jsp?id="+ids[i]+"'>"+names[i]+"</a> <img name='"+id+"_img_tag'   id='"+ids[i]+"_2' style='cursor:pointer' src='public/jQuery/easyui/css/images/tabs_close.gif' onclick=\"deleteFile('" + ids[i] + "','"+id+"')\" >";
				}
				desc.innerHTML=html;
				inp.value=inpVal;
			}
			obj.close();
		}
		
	</script>
</head>
<body>
	<div class="container">
		<div class="contr">
			<h2>You can select the file (image) and click Upload button</h2>
		</div>
		<div class="upload_form_cont">
			<form id="upload_form" enctype="multipart/form-data" method="post" action="upload.php">
				<div>
					<div>
						<label for="image_file">Please select image file</label>
					</div>
					<div>
						<input type="file" name="image_file" id="image_file" onchange="fileSelected();" />
					</div>
				</div>
				<div>
					<input type="button" value="Upload" onclick="startUploading('<%=sId %>','<%=request.getParameter("number") %>')" />
				</div>
				<div id="fileinfo">
					<div id="filename"></div>
					<div id="filesize"></div>
					<div id="filetype"></div>
					<div id="filedim"></div>
				</div>
				<div id="error">You should select valid image files only!</div>
				<div id="error2">An error occurred while uploading the file</div>
				<div id="abort">The upload has been canceled by the user or the browser dropped the connection</div>
				<div id="warnsize">Your file is very big. We can't accept it. Please select more small file</div>
				<div id="progress_info">
					<div id="progress"></div>
					<div id="progress_percent">&nbsp;</div>
					<div class="clear_both"></div>
					<div>
						<div id="speed">&nbsp;</div>
						<div id="remaining">&nbsp;</div>
						<div id="b_transfered">&nbsp;</div>
						<div class="clear_both"></div>
					</div>
					<div id="upload_response"></div>
				</div>
			</form>
			<img id="preview" />
		</div>
	</div>
<%
	String sql="select * from sys_file a where a.session_id= ?";

%>
	
  <div id="QM_sys_file_panel" style=""></div>
</body>
</html>