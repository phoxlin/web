function createComponent(name,json,oneline){
	//<input type="text" id="<%=hControlName%>" name="<%=hControlName%>" style="width:60%" class="form-control easyui-validatebox input-sm" data-options="required:true"  placeholder="<%=hControlTitle%>" value="<%=defaultVal%>">
	
	var content='';
	if(json.controlType=='easyui-combobox'){
		var tpl=document.getElementById('msEasyui-comboboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}else if(json.controlType=='easyui-combotree'){
		var tpl=document.getElementById('msEasyui-combotreeTpl').innerHTML;
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
	} else if(json.controlType=='password') {
		var tpl=document.getElementById('msPasswordTpl').innerHTML;
		content=template(tpl, {data:json,name:name,oneline:oneline});
	} else{
		var tpl=document.getElementById('msEasyui-validateboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,oneline:oneline});
	}
	return content;
}

function getMSDatas(data){
	//根据data里面的name和ms_name里面来获取edit页面的数据内容
}

function saveOrUpdate(data){
	var flag = true;
	$.ajax({
		type: "POST",
		url: "" + route + "/add",
		data: $("#" +ms_name + "_Form").serialize(),
		dataType: "json",
		beforeSend: function(xhr, settings){
			var valid = $("#" +ms_name + "_Form").form('validate');
			flag = valid;
			return valid;
		},
		async: false,
		success: function(data) {
			if(data.rs == "Y") {
				callback_info("修改成功",function(){
					location.reload();
				});				
			}
			else {
				flag = false;
				error(data.rs);
			}

		}
	});
	return flag;
}

function addDialogHook(){
	
}
//添加
function addDialog(){
	$.ajax({
		type : 'POST',
		url : controller+'-openAddDialog/'+ms_name+'/null',
		dataType : 'json',
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				var list=data.list;
				var columnNumber=data.columnNumber;
				var msDetailTpl=document.getElementById('msDetailTpl').innerHTML;
	        	var content=template(msDetailTpl, {list:data.list,name:ms_name,columnNumber:columnNumber,width:(dialog_width-50),height:(dialog_height-40)});

				art.dialog({
				    title: dialog_title,
				    width: dialog_width,
				    height: dialog_height,
				    content: content,
				    init: function () {
				    	$.parser.parse('#'+ms_name+'_Form');
				    	addDialogHook();
				    	
				    },
			        lock: true,
				    ok: function(){
				    	saveOrUpdate(data);
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


function updateDialogHook(){};

//修改
function updateDialog(){
	var selected = getSelectedCount();
	if(selected == 1){
		var id = getValuesByName('_id');
		$.ajax({
			type : 'POST',
			url : controller+"-openUpdateDialog/"+ms_name+"/"+id,
			dataType : 'json',
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					var list = data.list;
					var columnNumber=data.columnNumber;
					var msDetailTpl=document.getElementById('msDetailTpl').innerHTML;
		        	var content=template(msDetailTpl, {list:data.list,name:ms_name,columnNumber:columnNumber,width:(dialog_width-50),height:(dialog_height-40)});
					art.dialog({
						title: '修改'+dialog_title,
						width: dialog_width,
						height: dialog_height,
						content: content,
						init: function() {
							$.parser.parse('#'+ms_name+'_Form');
					    	var width=$('div[data-content-row="Y"]').width()-90;
					    	
					    	var onlineLabels=$('label[data-onlinelabel="Y"]');
					    	if(onlineLabels.length>0){
					    		var labelWidth=$('label[data-onlinelabel="N"]').width();
					    		onlineLabels.width(labelWidth);
					    		$('[data-onlineValue="Y"]').width(width-labelWidth);
					    	}
							updateDialogHook(data.list);
						},
						lock: true,
						ok: function(){
							var flag = saveOrUpdate();
							return flag;
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

//删除
function del(){
	var selected = getSelectedCount();
	if(selected <= 0){
		error("至少选择一条数据进行操作");
	}else{
		art.dialog({
			title: '确认删除'+dialog_title,
			content: '你确定要删除选择的信息吗?',
			icon: 'question',
			lock: true,
			okVal: "确定",
			ok: function(){
				doDel();
			},
			cancelVal: "取消",
			cancel : function() {
				return true;
			}
		});
	}
}

//确认删除
function doDel(){
	var id = getValuesByName('_id');
	$.ajax({
		type : 'POST',
		url : controller+"-del/"+ms_name,
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
//详细信息
function detailDialog(){
	var selected = getSelectedCount();
	if(selected == 1){
		
		var id = getValuesByName('_id');
		$.ajax({
			type : 'POST',
			url : controller+'-openDetailDialog/'+ms_name+'/'+id,
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

function onDblClick(index){
	
}


//第一次加载页面
function initPage(){
	$.ajax({
		type : 'POST',
		url : controller,
		dataType : 'json',
		data : {
			"name" : ms_name,
			"head": "Y",
			"filter": JSON.stringify(filter)
		},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				var pageTpl=document.getElementById('msIndexTpl').innerHTML;
//				data.total = data.total - (data.pagesize-data.rows.length);
	        	var html=template(pageTpl, {list:data,table_height:table_height,filter:filter,name:ms_name});
	        	$('#'+ms_name+'Page').html(html);
	        	$.parser.parse('#'+ms_name+'Page');
	        	$('#'+ms_name+'Page_table').datagrid({
	        		onDblClickCell: function(index,field,value){
	        			onDblClick(index);
	        		}
	        	});
	        	//排序
	        	sortCol();
	        	//翻页
	        	page(data.total);
	        	
			}else{
				error(result);
			}
		},
		error : function(xhr, type) {
			error("请联系管理员处理");
		}
	});
}

//翻页
function page(total){
	var pager = $('#'+ms_name+'Page_table').datagrid('getPager');
	$(pager).pagination({
		'total': total,
		onSelectPage: function(pageNumber, pageSize){
			
	        var queryParams = getQueryParams(); 
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
    				"name" : ms_name,
    				page: pageNumber,
    				rows: pageSize,
    				params: p
    			},
    			success : function(data) {
    				var result = "当前系统繁忙";
					result = data.rs;
    				if (result == 'Y') {
//    					console.log(data);
				        var options = $('#'+ms_name+'Page_table').datagrid('options');
				        options.pageNumber = pageNumber;   
				        options.pageSize = pageSize;   
				        $('#'+ms_name+'Page_table').datagrid('loadData',data);
    				}
    			},
    			error : function(xhr, type) {
    				error("请联系管理员处理");
    			}
    		});
		}
    }); 
}

//排序
function sortCol(){
	var queryParams = getQueryParams(); 
    queryParams = JSON.stringify(queryParams);
    
    var p = "";
    if(queryParams != null && queryParams.length > 0 && queryParams !== "{}"){
		p = queryParams;
    }
    
    var options = $('#'+ms_name+'Page_table').datagrid('options');
    var pageSize = options.pageSize;
	
	$('#'+ms_name+'Page_table').datagrid({onSortColumn: function(sort, order){
		$.ajax({
			type : 'POST',
			url : controller,
			dataType : 'json',
			data : {
				"name" : ms_name,
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

			        var options = $('#'+ms_name+'Page_table').datagrid('options');
			        options.pageNumber = 1;   
			        options.pageSize = pageSize;   
			        $('#'+ms_name+'Page_table').datagrid('loadData',data);
				}
			},
			error : function(xhr, type) {
				error("请联系管理员处理");
			}
		});
	}});
}

//筛选
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

//获取筛选条件       
function getQueryParams() {
	var queryParams={};
	for(var i=0; i<filter.length; i++){
		var item = filter[i];
		var columnname = item.columnname;
		var id = item.rownum+"_"+item.colnum+"_"+columnname;
		var val = $("#"+id).val();
		//$(element).get(0).tagName
		if(val != null && val.length > 0){
		    queryParams[columnname] = {"compare":item.compare,"value":val};  
		}
	}
  return queryParams;  
}


//获取选中行数
function getSelectedCount(){
	var selected = $('#'+ms_name+'Page_table').datagrid('getSelections');
	return selected.length;
}

//根据字段名字获取选择行的值
function getValuesByName(name){
	var selected = $('#'+ms_name+'Page_table').datagrid('getSelections');
	var values = new Array();
	for(var i=0; i<selected.length; i++){
		var item = selected[i];
		values.push(item[name]);
	}
	if(values != null && values.length == 1){
		values = values[0];
	}else{
		values = values.join(",");
	}
	return values;
}

//提示错误弹出框
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

//提示成功弹出框
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

//提示成功并执行回调
function callback_info(msg,fun){
	art.dialog(msg, function(){
		eval(fun());
	});
}
//确认提示框
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