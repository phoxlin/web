var initpageIndex=0;
window.localStorage.setItem("leftParams",'');
function initPage() {
	cq(ms_name);
}


function cExpert(name, type, params, startpage, pagesize, orderby, desc) {
	var leftParams = window.localStorage.getItem("leftParams");
	if(leftParams!=null&&leftParams.length>0){
		try{
			params=JSON.parse(leftParams);
		}catch(e){}
	}
	
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	if (typeof (type) == "undefined") {
		type = 'ms';
	}

	
	if (typeof (startpage) == "undefined") {
		startpage = 1;
	}
	if (typeof (pagesize) == "undefined") {
		pagesize = 20;
	}
	if (typeof (orderby) == "undefined") {
		orderby = '';
	}
	if (typeof (desc) == "undefined") {
		desc = 'n';
	}

	try {
		if (!filter) {
			filter = eval(name + '_filter');
		}
	} catch (e) {
		filter = [];
	}
	
	if (typeof (params) == "undefined") {
		try {
			params = eval(name + '_params');
			params = JSON.stringify(params);
		} catch (e) {
			params = '{}';
		}
	} else {
		if (isJson(params)) {
			params = JSON.stringify(params);
		}
	}

	for (var i = 0; i < filter.length; i++) {
		var f = filter[i];
		var fname = name + '_' + f.columnname + '_search_'+i;

		if ((f.format != null && f.format.length > 0) || 'date' == f.type
				|| 'datetime' == f.type) {
			try {
				var val = $('#' + fname).datebox('getValue');
				f.columnvalue = val
			} catch (e) {
				f.columnvalue = '';
			}
		} else {
			var val2 = $('#' + fname).val();
			f.columnvalue = val2;
		}

	}

	var exportIframe = document.createElement("iframe");
	exportIframe.width = 0;
	exportIframe.height = 0;
	exportIframe.style.display = "none";
	document.body.appendChild(exportIframe);

	var postForm = document.createElement("form");
	postForm.method = "post";
	postForm.target = exportIframe;

	var nameInput = document.createElement("input");
	nameInput.name = "name";
	nameInput.value = name;
	postForm.appendChild(nameInput);

	var typeInput = document.createElement("input");
	typeInput.name = "type";
	typeInput.value = type;
	postForm.appendChild(typeInput);

	var paramsInput = document.createElement("input");
	paramsInput.name = "params";
	paramsInput.value = params;
	postForm.appendChild(paramsInput);

	var startpageInput = document.createElement("input");
	startpageInput.name = "startpage";
	startpageInput.value = startpage;
	postForm.appendChild(startpageInput);

	var pagesizeInput = document.createElement("input");
	pagesizeInput.name = "pagesize";
	pagesizeInput.value = pagesize;
	postForm.appendChild(pagesizeInput);

	var orderbyInput = document.createElement("input");
	orderbyInput.name = "orderby";
	orderbyInput.value = orderby;
	postForm.appendChild(orderbyInput);

	var descInput = document.createElement("input");
	descInput.name = "desc";
	descInput.value = desc;
	postForm.appendChild(descInput);

	var filterInput = document.createElement("input");
	filterInput.name = "filter";
	filterInput.value = JSON.stringify(filter);
	postForm.appendChild(filterInput);

	postForm.action = 'common_query-expertExcel';
	document.body.appendChild(postForm);
	postForm.submit();
	document.body.removeChild(postForm);
	document.body.removeChild(exportIframe);
}

function cq(name, type, params, startpage, pagesize, orderby, desc) {
	
	var leftParams = window.localStorage.getItem("leftParams");
	if(leftParams!=null&&leftParams.length>0){
		try{
			params=JSON.parse(leftParams);
		}catch(e){}
	}
	
	
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	
	if (typeof (type) == "undefined") {
		type = 'ms';
	}
	
	var my_title="";
	if(type!='task'){
		try{
			if (typeof (dialog_title) == "undefined") {
				my_title=eval(name+'_dialog_title');
			}else{
				my_title=dialog_title;
			}
		}catch(e){
		}
	}
	try {
		loading = eval(name + '_loading');
	} catch (e) {
		loading = false;
	}
	

	
	if (typeof (startpage) == "undefined") {
		startpage = 1;
	}
	if (typeof (pagesize) == "undefined") {
		pagesize = 20;
	}
	if (typeof (orderby) == "undefined") {
		orderby = '';
	}
	if (typeof (desc) == "undefined") {
		desc = 'n';
	}

	try {
		filter = eval(name + '_filter');
	} catch (e) {
		filter = [];
	}
	
	if (typeof (params) == "undefined") {
		try {
			params = eval(name + '_params');
			params = JSON.stringify(params);
		} catch (e) {
			params = '{}';
		}
	} else {
		if (isJson(params)) {
			params = JSON.stringify(params);
		}
	}
	for (var i = 0; i < filter.length; i++) {
		var f = filter[i];
		var fname = name + '_' + f.columnname + '_search_'+i;

		if ((f.format != null && f.format.length > 0) || 'date' == f.type
				|| 'datetime' == f.type) {
			try {
				var val = $('#' + fname).datebox('getValue');
				f.columnvalue = val
			} catch (e) {
				f.columnvalue = '';
			}
			;

		} else {
			var val2 = $('#' + fname).val();
			f.columnvalue = val2;
		}

	}
	if (loading) {
		$.messager.progress();
	}
	$.ajax({
				type : 'POST',
				url : 'common_query',
				dataType : 'json',
				data : {
					name : name,
					params : params,
					type : type,
					startpage : startpage,
					pagesize : pagesize,
					orderby : orderby,
					desc : desc,
					filter : JSON.stringify(filter)
				},
				success : function(data) {
					if (loading) {
						$.messager.progress('close');
					}
					var result = "当前系统繁忙";
					result = data.rs;
					if (result == 'Y') {
						
						if (document.getElementById(data.name+"_table") != undefined) {
							//说明不是第一次加载，所以只需要刷新数据就可以了
							var common_query_Index_dataOnlyTpl = document.getElementById('common_query_Index_dataOnlyTpl').innerHTML;
							var common_query_Index_dataOnlyTplHtml = template(common_query_Index_dataOnlyTpl, {
								list : data,
								filter : filter,
								name : data.name,
								params : params,
								type : type,
								dialog_title:my_title,
								cds:data.cds
							});
							$('#' + data.name+"_responsive_tableAndpaging").html(common_query_Index_dataOnlyTplHtml);
							
							var pagerTpl = document.getElementById('common_query_Index_pagerTpl').innerHTML;
							var pager = template(pagerTpl, {
								list : data,
								filter : filter,
								name : data.name,
								params : params,
								type : type,
								dialog_title:my_title,
								cds:data.cds
							});
							$('#' + data.name+"_responsive_tablepager").html(pager);
						}else{
							//查看下是否是收银台的url如果是 就不显示数据导出功能
							
							var tVal=getQueryString("tt");
							var cashier=false;
							if('yp_browser'==tVal){
								cashier=true;
							}
							var pageTpl = document.getElementById('common_query_IndexTpl').innerHTML;
							var html = template(pageTpl, {
								list : data,
								filter : filter,
								name : data.name,
								params : params,
								type : type,
								dialog_title:my_title,
								cashier:cashier,
								cds:data.cds
							});
							if (document.getElementById(data.name) != undefined) {
								$('#' + data.name).html(html);
								$.parser.parse('#' + data.name);
							} else if (document.getElementById(data.name + "Page") != undefined) {
								$('#' + data.name + 'Page').html(html);
								$.parser.parse('#' + data.name + 'Page');
							}
							for (var i = 0; i < filter.length; i++) {
								var f = filter[i];
								var fname = data.name + '_' + f.columnname + '_search';
								if ((f.format != null && f.format.length > 0) || 'date' == f.type || 'datetime' == f.type) {
									if (f.columnvalue == null || f.columnvalue.length <= 0) {
										$('#' + fname).datebox('setValue', "");
									} else {
										$('#' + fname).datebox('setValue', f.columnvalue);
									}
								} else {
									$('#' + fname).val(f.columnvalue);
								}
							}
						}
						
						//判断js hook是否存在，存在就调用
						try{eval(data.name+"Hook()"); }catch(e){}
						
						//datebox placeholder
						$(".filter-container").find(".easyui-datebox").each(function(){
							var placeholder = $(this).attr("data-attr");
							$(this).next(".datebox").children(".textbox-text").attr("placeholder", placeholder);
						});

						//计算数据区显示高度
						var h = $(".main2").height();
						var f = $(".filter-container").outerHeight(true);
						var t = $("thead").outerHeight(true);
						$(".data").height(h-f-t-18-h*0.05);
						
						//捕获tr事件
						$('#' + data.name+"_responsive_tableAndpaging").find(".data").find("tr").each(function(index, element){
							element.addEventListener('click',function(){row_click(data.name, index)},true);
						});
					} else {
						error(result);
					}
				},
				error : function(xhr, type) {
					if (loading) {
						$.messager.progress('close');
					}
				}
			});

}

function checkedAll(name) {
	var all = document.getElementById(name + "_all");
	var rows = document.getElementsByName(name + "_row");
	if (all.checked) {
		for (var i = 0; i < rows.length; i++) {
			rows[i].checked = true;
		}
	} else {
		for (var i = 0; i < rows.length; i++) {
			rows[i].checked = false;
		}
	}
}

function getCheckedRows(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var list = new Array();
	var rows = document.getElementsByName(name + "_row");
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		if (row.checked) {
			// sys_apps_sign_row_7
			var temp = row.id.split('_');
			list.push(temp[temp.length - 1]);
		}
	}
	return list;
}

function getAllValuesByName(field,name){
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var rows = document.getElementsByName(name + "_row");
	var values = new Array();
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
			// sys_apps_sign_row_7
		var temp = row.id.split('_');
		
		var temp = row.id.split('_');
		var ii=temp[temp.length - 1];
		var temp = name + "__" + field + "_" + ii;
		var obj = document.getElementById(temp);
		var val = "";
		if (obj != null) {
			val = obj.value;
			if (val == null || val.length <= 0) {
				val = "";
			}
		} else {
			alert("找不到通用查询表【" + name + "】 的" + field + "值");
		}
		values.push(val);
	}
	return values;
}

function getValuesByName(field, name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var list = getCheckedRows(name);
	var values = new Array();
	for (var i = 0; i < list.length; i++) {
		// sys_apps_sign__sign_time_html_0

		var temp = name + "__" + field + "_" + list[i];
		var obj = document.getElementById(temp);
		var val = "";
		if (obj != null) {
			val = obj.value;
			if (val == null || val.length <= 0) {
				val = "";
			}
		} else {
			alert("找不到通用查询表【" + name + "】 的" + field + "值");
		}
		values.push(val);
	}
	if (values.length == 1) {
		return values[0];
	} else {
		return values;
	}

}

function changed_click(name, i) {
	var all = document.getElementById(name + "_all");
	var c = document.getElementById(name + '_row_' + i);
	var allc = true;
	if (c.checked) {
		var rows = document.getElementsByName(name + "_row");
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			if (!row.checked) {
				allc = false;
				break;
			}
		}
	} else {
		allc = false;
	}
	all.checked = allc;
}

function row_click(name, i) {
	var all = document.getElementById(name + "_all");
	var c = $('#' + name + '_row_' + i);
	var id = name + "_row_" + i;
	var allc = true;
	var rows = document.getElementsByName(name + "_row");
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		if (row.id != id) {
			row.checked = false;
			allc = false;
		} else {
			row.checked = true;
		}
	}
	all.checked = allc;
}

// 获取选中行数
function getSelectedCount(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}

	var list = getCheckedRows(name);
	return list.length;
}

// ####################兼容以前的方法############################################################################

function createComponent(name, json, oneline) {
	// <input type="text" id="<%=hControlName%>" name="<%=hControlName%>"
	// style="width:60%" class="form-control easyui-validatebox input-sm"
	// data-options="required:true" placeholder="<%=hControlTitle%>"
	// value="<%=defaultVal%>">

	var content = '';
	if (json.controlType == 'easyui-combobox') {
		var tpl = document.getElementById('msEasyui-comboboxTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'easyui-combotree') {
		var tpl = document.getElementById('msEasyui-combotreeTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'easyui-datebox') {
		var tpl = document.getElementById('msEasyui-dateboxTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'easyui-datetimebox') {
		var tpl = document.getElementById('msEasyui-datetimeboxTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'textarea') {
		var tpl = document.getElementById('msTextareaTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'easyui-numberbox') {
		var tpl = document.getElementById('msEasyui-numberboxTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'easyui-timespinner') {
		var tpl = document.getElementById('msEasyui-timespinnerTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'upload') {
		var tpl = document.getElementById('msUploadTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else if (json.controlType == 'password') {
		var tpl = document.getElementById('msPasswordTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	} else {
		var tpl = document.getElementById('msEasyui-validateboxTpl').innerHTML;
		content = template(tpl, {
			data : json,
			name : name,
			oneline : oneline
		});
	}
	return content;
}


// 提示错误弹出框
function error(msg) {
	art.dialog({
		title : "出错啦",
		content : msg,
		lock : true,
		icon : 'face-sad',
		ok : function() {
			return true;
		}
	});
}

// 提示成功弹出框
function success(msg) {
	art.dialog({
		title : '提示',
		content : msg,
		lock : true,
		icon : 'face-smile',
		ok : function() {
			return true;
		}
	});
}

// 提示成功并执行回调
function callback_info(msg, fun) {
	art.dialog(msg, function() {
		eval(fun());
	});
}
// 确认提示框
function confirm2(title, fun) {
	art.dialog({
		title : '确认',
		content : title,
		icon : 'question',
		lock : true,
		okVal : "确定",
		ok : function() {
			eval(fun());
		},
		cancelVal : "取消",
		cancel : function() {
			return true;
		}
	});
}

//翻页
function pager(obj, event){
	event.stopPropagation();
	if(event.keyCode == "13") {
		var name = $(obj).attr("data-name");
		var type = $(obj).attr("data-type");
		var params = $(obj).attr("data-params");
		var totalpage = $(obj).attr("data-totalpage");
		var pagesize = $(obj).attr("data-pagesize");
		var orderby = $(obj).attr("data-orderby");
		var desc = $(obj).attr("data-desc");
		
		var page = $(obj).val();
		if(page){
			if(page <= 0){
				page = 1;
			} else if(page > totalpage){
				page = totalpage
			}
			cq(name,type,params,page,pagesize,orderby,desc);
		}
	}
}