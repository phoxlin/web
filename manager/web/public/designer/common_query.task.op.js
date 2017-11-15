/**
 * 用户选择器
 */

function openChooseUsers(name,chooseUrl,userRole,number){
	var url=chooseUrl+"?name="+name+"&userRole="+userRole+"&number="+number;
	art.dialog.open(url, {
		title : "用户选择",
		width : 800,
		height : 400,
		okVal : "确定",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			iframe.addUser(this, document);
			return false;
		}
	});
	
}

function delChooseUser(id,inputId){
	var _1=document.getElementById(id+"_1");
	var _2=document.getElementById(id+"_2");
	_1.parentNode.removeChild(_1);
	_2.parentNode.removeChild(_2);
	var old=document.getElementById(inputId).value;
	var temp=new Array();
	if(old!=null||old.length>0){
		var list=old.split(",");
		for(var i=0;i<list.length;i++){
			var o=list[i];
			if(o!=id){
				temp.push(o);
			}
		}
	}
	document.getElementById(inputId).value=temp.join(",");
}

/**
 * 文件上传
 */
// 'wl_ad__fk_file_id','jpg,gif',true,1
function uploadFiles(callbackId, exts, num,pic) {
	var hasId = typeof (callbackId) != "undefined";
	if (!hasId) {
		error("上传文件控件没有指定回填Id");
	}
	var hasPic = typeof (pic) != "undefined";
	var isPic=false;
	if(hasPic){
		if("true"==pic){
			isPic=true;
		}
	}
	
	var hasExt = typeof (exts) != "undefined";
	var extList = new Array();
	if (hasExt) {
		extList = exts.split(',');
	}
	var number = 1;
	var hasNum = typeof (num) != "undefined";
	if (hasNum) {
		number = num;
	}
	var url="public/pub/file_upload/index.jsp?id="+callbackId;
	if(extList.length>0){
		url+="&ext="+extList.join(",");
	}
	url+="&number="+number;
	
	if(isPic){
		url+="&pic=Y";
	}else{
		url+="&pic=N";
	}
	
	art.dialog.open(url, {
		title : "文件上传",
		width : 800,
		height : 400,
		okVal : "确定",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			iframe.addFile(this, document);
			return false;
		}
	});
}

function deleteFile(id,inputId){
	var _1=document.getElementById(id+"_1");
	var _2=document.getElementById(id+"_2");
	_1.parentNode.removeChild(_1);
	_2.parentNode.removeChild(_2);
	var old=document.getElementById(inputId).value;
	var temp=new Array();
	if(old!=null||old.length>0){
		var list=old.split(",");
		for(var i=0;i<list.length;i++){
			var o=list[i];
			if(o!=id){
				temp.push(o);
			}
		}
	}
	document.getElementById(inputId).value=temp.join(",");
}


//保存下一步提交方法
function forwardConfirm(formName){
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'taskview-doForward',
		onSubmit : function(param) {
			var toVal=$('#traned2Name').val();
			var userinfo=$("select[data-userinfo='"+toVal+"']").val();
			var confirmed=$('#forward_confirm').is(':checked');
			var isok=true;
			if(userinfo==null||userinfo.length<24){
				isok=false;
			}else{
				isok=confirmed;
			}
			param.userinfo=userinfo;
			return isok;
		},
		success : function(data) {
			$.messager.progress('close');
			var result = "当前系统繁忙";
			try { data = eval('(' + data + ')'); result = data.rs; } catch (e) { try { data = eval(data); result = data.rs; } catch (e1) {}}
			if (result == 'Y') {
				callback_info("提交成功", function (){
					location.reload();
				});
			} else {
				error(result);
			}
		},
		error:function(data){
			$.messager.progress('close');
			error('系统繁忙，请稍后刷新重试');
		}
	});
}


function saveDraft(formName){
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'taskview-saveDraft',
		onSubmit : function(param) {
			//保存草稿不用验证
			return true;
		},
		success : function(data) {
			$.messager.progress('close');
			var result = "当前系统繁忙";
			try { data = eval('(' + data + ')'); result = data.rs; } catch (e) { try { data = eval(data); result = data.rs; } catch (e1) {}}
			if (result == 'Y') {
				info("保存成功");
			} else {
				error(result);
			}
		},
		error:function(data){
			$.messager.progress('close');
			error('系统繁忙，请稍后刷新重试');
		}
	});
}

function saveAndClose(formName){
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'taskview-finish',
		onSubmit : function(param) {
			var isValid = $(this).form('validate');
			if (!isValid) {
				$.messager.progress('close');
			}
			return isValid;
		},
		success : function(data) {
			$.messager.progress('close');
			var result = "当前系统繁忙";
			try { data = eval('(' + data + ')'); result = data.rs; } catch (e) { try { data = eval(data); result = data.rs; } catch (e1) {}}
			if (result == 'Y') {
				callback_info("提交成功", function (){
					location.reload();
				});
			} else {
				error(result);
			}
		}
	});
}

function updataApproval(){
	var appr_con_basic_note=$("#appr_con_basic_note").val();
	var appr_limit_note=$("#appr_limit_note").val();
	var special_loan_application=$("#special_loan_application").val();
	var appro_infor_remark=$("#appro_infor_remark").val();
	var special_loan=$("#special_loan").val();
	$.ajax({
		url:"saveApproval_info",
		type:"post",
		data:{
			appr_con_basic_note:appr_con_basic_note,
			appr_limit_note:appr_limit_note,
			special_loan_application:special_loan_application,
			special_loan:special_loan,
			appro_infor_remark:appro_infor_remark,
		},
		dataType:"json",
		success:function(data){
			  if(data.rs=="Y"){
				  callback_info("修改成功", function (){
						location.reload();
					});
			  }else
				  error(data.rs);s
		},
		error:function(){
			error("系统繁忙，请稍后刷新重试")
		}
		
	})	
}
function save(formName){
	
	if ('approval_flow_Form'==formName){
		var date=$("#fd_loan_amount__allow_date").val();
		if(date==''){
			error("批准期数不能为空");
			return;
		}
	}
	
		$.messager.progress();
		$('#' + formName).form('submit', {
			url : 'taskview-save',
			onSubmit : function() {
				//修改不验证
				return true;
			},
			success : function(data) {
				$.messager.progress('close');
				var result = "当前系统繁忙";
				try { data = eval('(' + data + ')'); result = data.rs; } catch (e) { try { data = eval(data); result = data.rs; } catch (e1) {}}
				if (result == 'Y') {
					callback_info("修改成功", function (){
						location.reload();
					});
				} else {
					error(result);
				}
			}
		});
}
function saveAndForward(formName){
	 var caState=$("#caState").val();
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'taskview-forward',
		onSubmit : function(param) {
			if(formName=='approval_flow_Form'||formName=="approval_info_Form")
				return true;
				var isValid = $(this).form('validate');
				if (!isValid) {
					$.messager.progress('close');
				}
			return isValid;
		},
		success : function(data) {
			$.messager.progress('close');
			var result = "当前系统繁忙";
			try { data = eval('(' + data + ')'); result = data.rs; } catch (e) { try { data = eval(data); result = data.rs; } catch (e1) {}}
			if (result == 'Y') {
				var savedTables = data.savedTables;
				if (savedTables != null && savedTables.length > 0) {
					for (var i = 0; i < savedTables.length; i++) {
						var table = savedTables[i];
						var tablename = table.table_name;
						var id = table.table_id;
						$('#' + tablename + '__id').val(id);
						$('#' + tablename + '___id').val(id);
					}
				}
			
				var trans=data.trans;
				if(trans!=null&&trans.length>0){
					var jhProcess_forwardTpl=document.getElementById('jhTask_forwardTpl').innerHTML;
					var caReadonly=false;
					if(caState!='undefined'&&caState!='startCO'&&formName!="survey_prospect_Form"){
			    		 caReadonly=true;
			    	}
			
			    	var content=template(jhProcess_forwardTpl, {li:data,width:'600px',height:'260px',name:'forward_'+formName,caReadonly:caReadonly,curUserId:data.userId});
			    	art.dialog({
						title: '保存&amp;下一步',
						width: 600,
						height: 260,
						content: content,
						lock: true,
						init: function () {
					    	var tabId=data.decistionName+"_tabs";
					    	$('#forward_'+formName+'_tabs a').click(function(e) {
					    		$("#forward_confirm").removeAttr("checked");
								$(this).tab('show');
								var href=$(this).attr('data-href');
								var to=$(this).attr('data-tran2name');
								$('#traned2Name').val(to);
								$('div[role=tabpanel]').hide();
								$('#'+href).show();
							})
					    }
					});
			    	
				}
			} else {
				error(result);
			}
		}
	});
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

function info(msg){
	success(msg);
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

function showTaskViewCallback(taskcode,sid,isTask){
	
	
};


function showTaskView(taskcode,sId,isTask){
	var jh_process_reload=true;
	$.ajax({
		type : 'POST',
		url : 'taskview-index',
		data:{
			reload:jh_process_reload,
			taskcode:taskcode,
			sId:sId,
			jh_access_token:jinhuayun.jh_access_token
		},
		dataType : 'json',
		success : function(data) {
			var result = data.rs;
			if (result == 'Y') {
				var pageTpl=document.getElementById('jhTaskTpl').innerHTML;
				//isfinish 判断加入---  CA版本延生问题
				 var state=data.task.state;//默认是这个
				 var userType=data.task.userType;
				 var CaState=data.task.CaState;//默认是这个
				 if("ca"!=userType){
					 if(data.task.steps!=undefined&&data.task.steps.length>0){
						 var firsttask=data.task.steps[0].taskname;//第一name
						 if(state=='closed'){
							 //判断当前的是不是CA 版本--CA版本完成处理 用sys_task_instance 中的state为准
							 if("CA"==firsttask&&"closed"!=CaState){
								 state="waitview";
							 }  
						 }
				 	}
				}else {
					if(CaState=='nextCO_closed'){
						state="closed";
					}
				}
				 
				var html=template(pageTpl, {task:data.task,isTask:isTask,isfinish:state,userType:data.task.userType});
				$('#'+taskcode+'_jh_process_page').html(html);
				//$.parser.parse();
				var isCA=false;
				if(CaState) 
					isCA=true;
				$('.selectpicker').selectpicker({
					  size:8
					});
				$('.form_datetime').datetimepicker({
				    language:  'zh-CN',
				    weekStart: 1,
				    todayBtn:  1,
					autoclose: 1,
					todayHighlight: 1,
					});
				showTaskViewCallback(taskcode,sId,isTask,state,data.task.data,isCA);
			}else{
				alert(result);
			}
		},
		error : function(xhr, type) {
			alert('系统繁忙，请稍后刷新重试');
		}
	});
}


function add(name) {
	var instance_id=$("#instance_id").val();
	var my_title="";
	try{
		my_title=eval(name+'_dialog_title');
	}catch(e){
	}
	var nextpage="edit.jsp";
	try{
		nextpage=eval(name+'_nextpage');
	}catch(e){
	}
	var myWidth=800;
	try{
		myWidth=eval(name+'_dialog_width');
	}catch(e){
	}
	var myHeight=600;
	try{
		myHeight=eval(name+'_dialog_height');
	}catch(e){
	}
	var my_entity="";
	try{
		my_entity=eval(name+'_entity');
	}catch(e){
	}
	art.dialog.open(nextpage + "?type=add&instance_id="+instance_id,{
		title : '添加' + my_title,
		width : myWidth,
		height : myHeight,
		cancelVal : "关闭",
		cancel : function() {
			return true;
		},
		okVal : "保存",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			iframe.savaAddDialog(this, document,my_entity,name);
			return false;
		},
	});

}

function savaAddDialog(win,doc,entity,name){
	$.messager.progress();
	$('#' + form_id).form('submit',	{
		url : "task-cq-save?m=add" + "&e=" + entity+"&lockId="+lockId,
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
				callback_info( "保存成功", function (){
					win.close();
					doc.getElementById(name+'_refresh_toolbar').click();
				});
			} else {
				error(result);
			}
		}
	});
}

//提示成功并执行回调
function callback_info(msg, fun) {
	art.dialog(msg, function() {
		eval(fun());
	});
}

function savaEditDialog(win,doc,entity,name){
	$.messager.progress();
	$('#' + form_id).form('submit',	{
		url : "task-cq-save?m=edit" + "&e=" + entity+"&lockId="+lockId,
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
				callback_info( "保存成功", function (){
					win.close();
					doc.getElementById(name+'_refresh_toolbar').click();
				});
			} else {
				error(result);
			}
		}
	});
}

function edit(name) {
	var instance_id=$("#instance_id").val();
	var my_title="";
	var my_title="";
	try{
		my_title=eval(name+'_dialog_title');
	}catch(e){
	}
	var nextpage="edit.jsp";
	try{
		nextpage=eval(name+'_nextpage');
	}catch(e){
	}
	var myWidth=800;
	try{
		myWidth=eval(name+'_dialog_width');
	}catch(e){
	}
	var myHeight=600;
	try{
		myHeight=eval(name+'_dialog_height');
	}catch(e){
	}
	var my_entity="";
	try{
		my_entity=eval(name+'_entity');
	}catch(e){
	}
	var count = getSelectedCount(name);
	if (count == 1) {
		var id = getValuesByName("id",name);
		art.dialog.open("task-cq-detail?entity=" + my_entity + "&id=" + id+"&nextpage=" + nextpage + "&type=edit&instance_id="+instance_id,{
			title : '修改' + my_title,
			width : myWidth,
			height : myHeight,
			okVal : "修改",
			ok : function() {
				var iframe = this.iframe.contentWindow;
				iframe.savaEditDialog(this, document,my_entity,name);
				return false;
			},
			cancelVal : "关闭",
			cancel : function() {
				return true;
			}
		});
	} else {
		error("请选择一行信息进行编辑");
	}

}

function detail(name) {
	var my_title="";
	try{
		my_title=eval(name+'_dialog_title');
	}catch(e){
	}
	var nextpage="edit.jsp";
	try{
		nextpage=eval(name+'_nextpage');
	}catch(e){
	}
	var myWidth=800;
	try{
		myWidth=eval(name+'_dialog_width');
	}catch(e){
	}
	var myHeight=600;
	try{
		myHeight=eval(name+'_dialog_height');
	}catch(e){
	}
	var my_entity="";
	try{
		my_entity=eval(name+'_entity');
	}catch(e){
	}
	var count = getSelectedCount(name);
	if (count == 1) {
		var id = getValuesByName("id",name);
		art.dialog.open("task-cq-detail?entity=" + my_entity + "&id=" + id+"&nextpage=" + nextpage + "&type=detail",
				{
					title : '查看' + my_title,
					width : myWidth,
					height : myHeight,
					cancelVal : "关闭",
					cancel : function() {
						return true;
					}
				});
	} else {
		error("请选择一行信息进行编辑");
	}
}

// 删除
function del(name) {
	var my_title="";
	try{
		my_title=eval(name+'_dialog_title');
	}catch(e){
	}
	
	var selected = getSelectedCount(name);
	if (selected <= 0) {
		error("至少选择一条数据进行操作");
	} else {
		art.dialog({
			title : '确认删除' + my_title,
			content : '你确定要删除选择的信息吗?',
			icon : 'question',
			lock : true,
			okVal : "确定",
			ok : function() {
				doDel(name);
			},
			cancelVal : "取消",
			cancel : function() {
				return true;
			}
		});
	}
}

// 确认删除
function doDel(name) {
	var id = getValuesByName('id', name);
	
	var my_entity="";
	try{
		my_entity=eval(name+'_entity');
	}catch(e){
	}
	
	$.ajax({
		type : 'POST',
		url : "task-cq-del",
		dataType : 'json',
		data : {
			id : id + "",
			entity : my_entity+""
		},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				callback_info("删除成功啦", function() {
					$('#'+name+'_refresh_toolbar').click();
				});
			}else{
				error(result);
			}
		}
	});
}

function onDblClick(name) {
	detail(name);
}
function saveOrUpdate(data, name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var flag = true;
	$.ajax({
		type : "POST",
		url : "msLoad/add",
		data : $("#" + name + "_Form").serialize(),
		dataType : "json",
		beforeSend : function(xhr, settings) {
			var valid = $("#" + name + "_Form").form('validate');
			flag = valid;
			return valid;
		},
		async : false,
		success : function(data) {
			if (data.rs == "Y") {
				callback_info("修改成功", function() {
					location.reload();
				});
			} else {
				flag = false;
				error(data.rs);
			}

		}
	});
	return flag;
}

function processTask(path,sid/*sys_task_step表的id*/,state){
	//ie 10 有问题所以加了一个path
	var  url=path+'/pages/'+state+'/index.jsp?sid='+sid;
	window.open(url);
	//如果可以进行页面跳转。
}

function autoFetch(legendCode,task_step_id,img){
	var autoFunc=legendCode+"AutoFetch('"+legendCode+"','"+task_step_id+"');";
	try{eval(autoFunc);}catch(e){console.warn("没有定义自动加载任务方法："+autoFunc);}
}

