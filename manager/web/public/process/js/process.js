

function createProcessComponent(name,json,fieldColumn,column_clz,column_style){
	//<input type="text" id="<%=hControlName%>" name="<%=hControlName%>" style="width:60%" class="form-control easyui-validatebox input-sm" data-options="required:true"  placeholder="<%=hControlTitle%>" value="<%=defaultVal%>">
	var content='';
	if(json.controltype=='easyui-combobox'){
		var tpl=document.getElementById('processEasyui-comboboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,fieldColumn:fieldColumn,column_clz:column_clz,column_style:column_style});
	}else{
		var tpl=document.getElementById('processEasyui-validateboxTpl').innerHTML;
    	content=template(tpl, {data:json,name:name,fieldColumn:fieldColumn,column_clz:column_clz,column_style:column_style});
	}
	
	return content;
}

function autoFetch(legendName,task_instrance_id,dataSourceName,img){
	console.log(legendName+" "+task_instrance_id+" "+dataSourceName);
	$.ajax({
		type : 'POST',
		url : 'process-autoFetch',
		data:{
			task_instrance_id:task_instrance_id,
			legendName:legendName,
			dataSourceName:dataSourceName
		},
		beforeSend: function(){
		     img.src='public/process/tpl/default/img/loading.gif'
		},
		complete: function(){
			img.src='public/process/tpl/default/img/autoLoad.png'
		},
		dataType : 'json',
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				var one2one=data['one2one'];
				if(one2one){
					for (var key in one2one) {
						$("#"+key).val(one2one[key])
					}
				}
				if(data.refreshGridName){
					$('#'+data.refreshGridName+'_refresh_toolbar').click();
				}
				
			}
		},
		error : function(xhr, type) {
			console.log('error');
			error("请联系管理员处理");
		}
	});
}


function forwardConfirm(){
	var isChecked=$('#forward_confirm').is(':checked');
	var users=$("[name='forward_users']");
	var userId="";
	for(var i=0;i<users.length;i++){
		var u=users[i];
		if(u.checked){
			userId=u.value;
		}
	}
	if(userId!=null&&userId.length>0){
		if(isChecked){
			var forward_remark=$('#forward_remark').val();
			var traned2Name=$('#traned2Name').val();
			var task_instrance_id=$('#task_instrance_id').val();
				
			$.ajax({
				type : 'POST',
				url : 'process-transfered',
				data:{
					userId:userId,
					forward_remark:forward_remark,
					traned2Name:traned2Name,
					task_instrance_id:task_instrance_id
				},
				dataType : 'json',
				success : function(data) {
					var result = "当前系统繁忙";
					result = data.rs;
					if (result == 'Y') {
						info("流程处理成功");
					}
				},
				error : function(xhr, type) {
					error("请联系管理员处理");
				}
			});
		}else{
			error("请先确认操作");	
		}
	}else{
		error("请选择一个同事");	
	}
	
	
	
	
}

function saveAndForward(formName) {
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'process-forward',
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
					var jhProcess_forwardTpl=document.getElementById('jhProcess_forwardTpl').innerHTML;
			    	var content=template(jhProcess_forwardTpl, {li:data,width:'600px',height:'260px',name:'forward_'+formName});
			    	art.dialog({
						title: data.decistionName+' - 审批环节',
						width: 600,
						height: 260,
						content: content,
						lock: true,
						init: function () {
					    	var tabId=data.decistionName+"_tabs";
					    	$('#'+tabId+' a').click(function(e) {
					    		$("[name='forward_users']").removeAttr("checked");
					    		$('#traned2Name').val($(this).attr('data-tran2name'));
								$(this).tab('show');
								var id=$(this).attr('data-href');
								$('div[role=tabpanel]').hide();
								$('#'+id).show();
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

function saveAndClose(formName) {

}

function saveDraft(formName) {
	$.messager.progress();
	$('#' + formName).form('submit', {
		url : 'process-draft',
		onSubmit : function(param) {
			return true;
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

				info("操作成功");
			} else {
				error(result);
			}
		}
	});
}