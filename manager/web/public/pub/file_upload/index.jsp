<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String sessionId=UUID.randomUUID().toString();
%>
<!DOCTYPE HTML>
<html>
<head>
<jsp:include page="/public/base.jsp" />
<link href="public/pub/file_upload/pure.css" rel="stylesheet" type="text/css" />
<script src="public/pub/file_upload/pure.js"></script>

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
	
	<script type="text/javascript">


//data-grid配置开始
///////////////////////////////////////////(1).sys_file_upload___sys_file开始///////////////////////////////////////////
	//搜索配置
	var sys_file_upload___sys_file_filter=[
				      	 ];
	//编辑页面弹框标题配置
	var sys_file_upload___sys_file_dialog_title='文件上传';
	//编辑页面弹框宽度配置
	var sys_file_upload___sys_file_dialog_width=700;
	//编辑页面弹框高度配置
	var sys_file_upload___sys_file_dialog_height=500;
	//IndexGrid数据加载提示配置
	var sys_file_upload___sys_file_loading=true;
	//编辑页面弹框宽度配置
	var sys_file_upload___sys_file_entity="sys_file";
	//编辑页面路径配置
	var sys_file_upload___sys_file_nextpage="pages/sys_file_upload/sys_file_edit.jsp";
	<%
		String sql="select * from sys_file a where a.session_id = ?";
	%>

	var sys_file_upload___sys_file_params={sql:"<%=sql%>",sqlPs:['<%=sessionId %>']};
///////////////////////////////////////////(1).sys_file_upload___sys_file结束///////////////////////////////////////////

//data-grid配置结束

</script>

<script type="text/javascript">
	$(document).ready(function() {
		showTaskView('sys_file_upload','','N');
	});
</script>
</head>
<body>

    <div class="container-fluid">
		<div class="row">
		<div class="upload_form_cont">
			<form id="upload_form" enctype="multipart/form-data" method="post">
				<div>
					<div>
						<label for="image_file">Please select image file</label>
					</div>
					<div>
						<input type="file" name="image_file" id="image_file" onchange="fileSelected();" />
					</div>
				</div>
				<div>
					<input type="button" value="Upload" onclick="startUploading('<%=sessionId%>','<%=request.getParameter("number") %>')" />
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
				</div>
			</form>
		</div>
	</div>
	</div>
	<div class="row">
 		<div id="sys_file_upload_jh_process_page"> </div>
  	</div>
</body>
</html>