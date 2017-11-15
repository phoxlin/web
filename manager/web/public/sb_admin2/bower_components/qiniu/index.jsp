<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>通用文件上传</title>
	<jsp:include page="/public/base.jsp"></jsp:include>
</head>
<body>
    <div class="container">
		<input id="uploaded_file_count_limited" type="hidden" value="-1" >
	     <div class="row" style="margin-top: 10px;">
	         <div class="col-md-12">
	             <div id="container">
	                 <a class="btn btn-default btn-lg " id="pickfiles" href="#" >
	                     <i class="glyphicon glyphicon-plus"></i>
	                     <span>选择文件</span>
	                 </a>
	             </div>
	         </div>
	         <div style="display:none" id="success" class="col-md-12">
	             <div class="alert-success">
	                 队列全部文件处理完毕
	             </div>
	         </div>
	         <div class="col-md-12 ">
	             <table class="table table-striped table-hover text-left"   style="margin-top:10px;display:none">
	                 <thead>
	                   <tr>
	                     <th class="col-md-4">文件名</th>
	                     <th class="col-md-3" ></th>
	                     <th class="col-md-5">详细信息</th>
	                   </tr>
	                 </thead>
	                 <tbody id="fsUploadProgress">
	                 </tbody>
	             </table>
	         </div>
	     </div>

    </div>

<script type="text/javascript">hljs.initHighlightingOnLoad();</script>
<script type="text/javascript">
	function GetRequest() {   
	   var url = location.search; //获取url中"?"符后的字串   
	   var theRequest = new Object();   
	   if (url.indexOf("?") != -1) {   
	      var str = url.substr(1);   
	      strs = str.split("&");   
	      for(var i = 0; i < strs.length; i ++) {   
	         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);   
	      }   
	   }   
	   return theRequest;   
	}   
	var params=GetRequest();
	if(params.count!=null&&params.count>0){
		$('#uploaded_file_count_limited').val(params.count);
	}
</script>

</body>
</html>
