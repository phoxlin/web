<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">


<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">


<link href="public/sb_admin2/bower_components/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
<!--
	var webRoot = '${pageContext.request.contextPath}';
//-->
</script>

<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/easyui-1.4.4/themes/bootstrap/easyui.css">
<link rel="stylesheet" type="text/css" href="public/sb_admin2/bower_components/easyui-1.4.4/themes/icon.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<!-- jQuery -->
<script src="public/sb_admin2/bower_components/jquery/dist/jquery.min.js"></script>


<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/jquery.easyui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/easyui-1.4.4/myValidate.js"></script>
<script type="text/javascript" charset="utf-8" src="public/sb_admin2/bower_components/bootstrap/js/bootstrap-select.min.js"></script>

<script type="text/javascript" charset="utf-8" src="public/js/artDialog.js?skin=default"></script>
<script type="text/javascript" charset="utf-8" src="public/js/iframeTools.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/artDialog.notice.source.js"></script>

<script type="text/javascript" charset="utf-8" src="public/js/jinhua-yun-1.0.0.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/json2.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/template.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/kindeditor/kindeditor-all-min.js"></script>
<script type="text/javascript" charset="utf-8" src="public/js/uuid.js"></script>
<script type="text/javascript" charset="utf-8" src="public/designer/common_query.task.op.js"></script>


<script type="text/html" id="upload_result_tpl">
		<#
			for(var i=0;i<info.length;i++){
				var file=info[i];
				var url=file.url;
				var filename=file.filename;
				var baseId=field_name+"_"+i;
				var id="_1"+field_name+"_"+i;
				var id2="_2"+field_name+"_"+i;
				#>
				
				<a id='<#=id#>' href='<#=url#>' ><#=filename#></a>
				<img id='<#=id2#>' style='cursor:pointer' src='/public/sb_admin2/bower_components/qiniu/images/del.png' onclick="deleteFile('<#=baseId#>','<#=field_name#>')" >
				<#
			}
		#>
</script>
<script type="text/javascript">
<!--
	template.config({
		sTag : '<#', eTag: '#>'
	});
//-->
</script>
