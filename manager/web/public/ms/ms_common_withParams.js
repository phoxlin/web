function createComponent(name,json,oneline){
	// <input type="text" id="<%=hControlName%>" name="<%=hControlName%>"
	// style="width:60%" class="form-control easyui-validatebox input-sm"
	// data-options="required:true" placeholder="<%=hControlTitle%>"
	// value="<%=defaultVal%>">
	
	var content='';
	if(json.controlType=='easyui-combobox'){
		var tpl=document.getElementById('msEasyui-comboboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-combotree'){
		var tpl=document.getElementById('msEasyui-comboboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-datebox'){
		var tpl=document.getElementById('msEasyui-dateboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-datetimebox'){
		var tpl=document.getElementById('msEasyui-datetimeboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='textarea'){
		var tpl=document.getElementById('msTextareaTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-numberbox'){
		var tpl=document.getElementById('msEasyui-numberboxTpl').innerHTML;
		content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-timespinner') {
		var tpl=document.getElementById('msEasyui-timespinnerTpl').innerHTML;
		content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='upload') {
		var tpl=document.getElementById('msUploadTpl').innerHTML;
		content=template(tpl, {data:json,name:name,oneline:oneline});
	} else{
		var tpl=document.getElementById('msEasyui-validateboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}
	return content;
}

function edit(name,type,actionURL){
	if (typeof (type) == "undefined") {
		type = "qm";
	}
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	if (typeof (actionURL) == "undefined") {
		actionURL = controller;
	}
	if('ms'==type){
		var dialog_width=800;
		var dialog_height = 600;
		var dialog_title="未设置";
		try{ dialog_width=eval(name+'_dialog_width'); }catch(e){}
		try{ dialog_height=eval(name+'_dialog_height'); }catch(e){}
		try{ dialog_title=eval(name+'_dialog_title'); }catch(e){}
		updateDialogWithName(name,type,actionURL,dialog_width,dialog_height,dialog_title);
	}else if('qm'==type){
		qm_add(name);
	}
}

// 修改
// 修改传递参数
function updateDialogWithName(name,type,actionURL,dialog_width,dialog_height,dialog_title){
	var selected = getSelectedCountWithName(name);
	if(selected == 1){
		var id = null;
		if(type=='ms'){
			id=getValuesByNameWithName(name,'_id');
		}else{
			id=getValuesByNameWithName(name,'id');
		}
		$.ajax({
			type : 'POST',
			url : actionURL+"-openUpdateDialog/"+name+"/"+id,
			dataType : 'json',
			data : {
				"name" : name,
				"id" : id
			},
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					var list=data.list;
					var columnNumber=data.columnNumber;
					var msDetailTpl=document.getElementById('msDetailTpl').innerHTML;
		        	var content=template(msDetailTpl, {list:data.list,name:name,columnNumber:columnNumber,width:(dialog_width-50),height:(dialog_height-40)});

					art.dialog({
					    title: '修改 - '+dialog_title,
					    width: dialog_width,
					    height: dialog_height,
					    content: content,
					    init: function () {
					    	$.parser.parse('#'+name+'_Form');
					    	editDialogHookWithName(name);
					    	
					    },
				        lock: true,
					    ok: function(){
					    	saveOrUpdateWithName(data , name);
					    	return false;
					    }
					});
					
				}
			},
			error : function(xhr, type) {
				error("请联系管理员处理");
			}
		});
	}else{
		error("请选择一条数据进行操作");
	}
}


function del(name,type,actionURL){
	if (typeof (type) == "undefined") {
		type = "qm";
	}
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	if (typeof (actionURL) == "undefined") {
		actionURL = controller;
	}
	
	var dialog_title="未设置";
	try{ dialog_title=eval(name+'_dialog_title'); }catch(e){}
	
	// 删除
	var selected = getSelectedCountWithName(name);
	if(selected <= 0){
		error("至少选择一条数据进行操作");
	}else{
		art.dialog({
			title: '确认删除 - '+dialog_title,
			content: '你确定要删除选择的信息吗?',
			icon: 'question',
			lock: true,
			okVal: "确定",
			ok: function(){
				doDelWithName(name,type,actionURL);
			},
			cancelVal: "取消",
			cancel : function() {
				return true;
			}
		});
	}
}

// 确认删除
function doDelWithName(name,type,actionURL){
	var id = null;
	if(type=='ms'){
		id=getValuesByNameWithName(name,'_id');
	}else{
		id=getValuesByNameWithName(name,'id');
	}
	$.ajax({
		type : 'POST',
		url : actionURL+'-del/'+name,
		dataType : 'json',
		data:{
			id:id+""
		},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				callback_info("删除成功啦",function(){
					location.reload();
				});
			}
		},
		error : function(xhr, type) {
			error("请联系管理员处理");
		}
	});
}




function add(name,type,actionURL){
	if (typeof (type) == "undefined") {
		type = "qm";
	}
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	if (typeof (actionURL) == "undefined") {
		actionURL = controller;
	}
	if('ms'==type){
		var dialog_width=800;
		var dialog_height = 600;
		var dialog_title="未设置";
		try{ dialog_width=eval(name+'_dialog_width'); }catch(e){}
		try{ dialog_height=eval(name+'_dialog_height'); }catch(e){}
		try{ dialog_title=eval(name+'_dialog_title'); }catch(e){}
		addDialogWithName(name,actionURL,dialog_width,dialog_height,dialog_title);
	}else if('qm'==type){
		qm_add(name);
	}
}

function addDialogWithName(name,actionURL,dialog_width,dialog_height,dialog_title){
	$.ajax({
		type : 'POST',
		url : actionURL+'-openAddDialog/'+name+'/null',
		dataType : 'json',
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				var list=data.list;
				var columnNumber=data.columnNumber;
				var msDetailTpl=document.getElementById('msDetailTpl').innerHTML;
	        	var content=template(msDetailTpl, {list:data.list,name:name,columnNumber:columnNumber,width:(dialog_width-50),height:(dialog_height-40)});

				art.dialog({
				    title: '添加 - '+dialog_title,
				    width: dialog_width,
				    height: dialog_height,
				    content: content,
				    init: function () {
				    	$.parser.parse('#'+name+'_Form');
				    	addDialogHookWithName(name);
				    	
				    },
			        lock: true,
				    ok: function(){
				    	saveOrUpdateWithName(data , name);
				    	return false;
				    }
				});
		        
			}
		},
		error : function(xhr, type) {
			error("请联系管理员处理");
		}
	});
}





function saveOrUpdateWithName(data, name){
	var flag = true;
	$.ajax({
		type: "POST",
		url: "" + route + "/add",
		data: $("#" + name + "_Form").serialize(),
		dataType: "json",
		beforeSend: function(xhr, settings){
			var valid = $("#" + name + "_Form").form('validate');
			flag = valid;
			return valid;
		},
		async: false,
		success: function(data) {
			if(data.rs == "Y") {
				callback_info("添加成功",function(){
					location.reload();
				});				
			}
			else {
				error(data.rs);
			}

		}
	});
	return false;
}

function addDialogHookWithName(name){}
function editDialogHookWithName(name){}

// 添加传表名





// 详细信息
function detailDialogWithName(name){
	var selected = getSelectedCountWithName(name);
	if(selected == 1){
		var id = null;
		if(type=='ms'){
			id=getValuesByNameWithName(name,'_id');
		}else{
			id=getValuesByNameWithName(name,'id');
		}
		$.ajax({
			type : 'POST',
			url : controller+'-openDetailDialog/'+name,
			dataType : 'json',
			data : {
				"id" : id
			},
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					var content = data.content;
					
					art.dialog({
						title: dialog_title+'详情',
						width: dialog_width,
						height: dialog_height,
						content: content,
						lock: true,
						cancelVal: "关闭",
						cancel : function() {
							return true;
						}
					});
				}
			},
			error : function(xhr, type) {
				error("请联系管理员处理");
			}
		});
	}else{
		error("请选择一条数据进行操作");
	}
}

function onDblClickWithName(name,index){
	console.log(name);
}

// 第一次加载页面

function initPageWithParams(pageName,control,myfilter){
	var table_height='600px';
	try{ table_height=eval(pageName+'_table_height'); }catch(e){}
	$.ajax({
		type : 'POST',
		url : control,
		dataType : 'json',
		data : {
			"name" : pageName,
			"head": "Y",
			"filter": JSON.stringify(myfilter)
		},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				var pageTpl=document.getElementById('msIndexTpl').innerHTML;
				var html=template(pageTpl, {list:data,table_height:table_height,filter:myfilter,name:pageName});
				$('#'+pageName+'Page').html(html);
				$.parser.parse('#'+pageName+'Page');
				// 排序
				sortColWithName(pageName,control,myfilter);
				// 翻页
				pageWithName(pageName,data.total,control,myfilter);
				$('#'+pageName+'Page_table').datagrid({
	        		onDblClickCell: function(index,field,value){
	        			onDblClickWithName(pageName,index);
	        		}
	        	});
			}else{
				error(result);
			}
		},
		error : function(xhr, type) {
			error("请联系管理员处理");
		}
	});
}



// 翻页
function pageWithName(name,total,controller,myfilter){
	var pager = $('#'+name+'Page_table').datagrid('getPager');
	$(pager).pagination({
		total: total,
		onSelectPage: function(pageNumber, pageSize){
			
			var queryParams = getQueryParamsWithFilter(myfilter); 
			queryParams = JSON.stringify(queryParams);
			
			var p = "";
			if(queryParams != null && queryParams.length > 0 && queryParams !== "{}"){
				p = queryParams;
			}
			
			$.ajax({
				type : 'POST',
				url : controller,
				dataType : 'json',
				data : {
					"name" : name,
					page: pageNumber,
					rows: pageSize,
					params: p
				},
				success : function(data) {
					var result = "当前系统繁忙";
					result = data.rs;
					if (result == 'Y') {
						
						var options = $('#'+name+'Page_table').datagrid('options');
						options.pageNumber = pageNumber;   
						options.pageSize = pageSize;   
						$('#'+name+'Page_table').datagrid('loadData',data);
					}
				},
				error : function(xhr, type) {
					error("请联系管理员处理");
				}
			});
		}
	}); 
}
    
// 排序
function sortColWithName(name,controller,filter){
	var queryParams = getQueryParamsWithFilter(filter); 
	queryParams = JSON.stringify(queryParams);
	
	var p = "";
	if(queryParams != null && queryParams.length > 0 && queryParams !== "{}"){
		p = queryParams;
	}
	
	var options = $('#'+name+'Page_table').datagrid('options');
	var pageSize = options.pageSize;
	
	$('#'+name+'Page_table').datagrid({onSortColumn: function(sort, order){
		$.ajax({
			type : 'POST',
			url : controller,
			dataType : 'json',
			data : {
				"name" : name,
				page: 1,
				rows: pageSize,
				params: p,
				sort: sort,
				order: order
			},
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					
					var options = $('#'+name+'Page_table').datagrid('options');
					options.pageNumber = 1;   
					options.pageSize = pageSize;   
					$('#'+name+'Page_table').datagrid('loadData',data);
				}
			},
			error : function(xhr, type) {
				error("请联系管理员处理");
			}
		});
	}});
}

// 筛选
function searchFor(name){
    var queryParams = getQueryParams(); 
    queryParams = JSON.stringify(queryParams);
    
    var p = "";
    if(queryParams != null && queryParams.length > 0 && queryParams !== "{}"){
		p = {"params":queryParams};
    }
    
    var options = $('#'+name+'Page_table').datagrid('options');
    var pageSize = options.pageSize;
    
	$('#'+name+'Page_table').datagrid({  
        url:controller+"?name="+ms_name,
        method: "POST",
        pageNumber: 1,
        pageSize: pageSize,
        "queryParams": p
    });
}

// 获取筛选条件
function getQueryParamsWithFilter(filter) {
	var queryParams={};
	for(var i=0; i<filter.length; i++){
		var item = filter[i];
		var columnname = item.columnname;
		var id = item.rownum+"_"+item.colnum+"_"+columnname;
		var val = $("#"+id).val();
		// $(element).get(0).tagName
		if(val != null && val.length > 0){
		    queryParams[columnname] = {"compare":item.compare,"value":val};  
		}
	}
  return queryParams;  
}


// 获取选中行数
function getSelectedCountWithName(name){
	var selected = $('#'+name+'Page_table').datagrid('getSelections');
	return selected.length;
}

// 根据字段名字获取选择行的值
function getValuesByNameWithName(m_name,fieldName){
	var selected = $('#'+m_name+'Page_table').datagrid('getSelections');
	var values = new Array();
	for(var i=0; i<selected.length; i++){
		values.push(selected[i][fieldName]);
	}
	if(values != null && values.length == 1){
		values = values[0];
	}
	return values;
}


// 提示错误弹出框
function error(msg) {
	art.dialog({
	    title: "出错啦",
	    content: msg,
	    lock: true,
	    icon: 'face-sad',
	    ok : function() {
			return true;
		}
	});
}
// 提示成功弹出框
function info(msg) {
	art.dialog({
	    title: '提示',
	    content: msg,
	    lock: true,
	    icon: 'face-smile',
	    ok : function() {
			return true;
		}
	});
}

// 提示成功弹出框
function success(msg) {
	art.dialog({
	    title: '提示',
	    content: msg,
	    lock: true,
	    icon: 'face-smile',
	    ok : function() {
			return true;
		}
	});
}

// 提示成功并执行回调
function callback_info(msg,fun){
	art.dialog(msg, function(){
		eval(fun());
	});
}
// 确认提示框
function confirm2(title, fun){
	art.dialog({
		title: '确认删除'+title,
		content: '你确定要删除选择的信息吗?',
		icon: 'question',
		lock: true,
		okVal: "确定",
		ok: function(){
			eval(fun());
		},
		cancelVal: "取消",
		cancel : function() {
			return true;
		}
	});
}