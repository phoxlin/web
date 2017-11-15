function backup_now(name){
	$.ajax({
		type : 'POST',
		url : "db-backup_now",
		dataType : 'json',
		data : {},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				callback_info("成功啦", function() {
					$('#'+name+'_refresh_toolbar').click();
				});
			}
		}
	});
}


function getBackByFile(name){
		art.dialog.open('public/pub/db/sys_db_record/uploadDBFile.jsp', {
			title : '上传数据库备份文件',
			width : 600,
			height : 200,
			okVal : "确定",
			cancelVal : "关闭",
			ok:function(){
				var iframe = this.iframe.contentWindow;
				iframe.saveDBFile(this, document);
				return false;
			},
			cancel : function() {
				return true;
			}
		});	
}


function getBack(name){
	var count = getSelectedCount(name,"id");
	if (count == 1) {
		var optype = getValuesByName("op_type__qm_code",name);
		if('001'!=optype){
			error("请选择一个备份文件进行恢复操作");    			
		}else{
			art.dialog({
				title : '提示',
				content : "你确定要用选择的备份文件恢复数据库吗？警告：此操作有可能造成数据丢失！确定执行吗？？",
				lock : true,
				ok : function() {
					doGetBack(name,this);
					return true;
				},
				okVal : "确定",
				cancel : function() {
					return true;
				},
				cancelVal : '取消'
			});
		}
	} else {
		error( "请选择一行信息进行编辑");
	}
}

function doGetBack(name,win){
	$.messager.progress(); 
	var id = getValuesByName("fk_file_id",name);
	$.ajax({
		type : "POST",
		url : "db-getback",
		dataType : "json",
		data : {
			id : id + ""
		},
		success : function(data) {
			$.messager.progress('close');
			var result="当前系统繁忙";try{data = eval('(' + data + ')');	result=data.rs;}catch(e){try{data = eval(data);result=data.rs;}catch(e1){}}
			if (result == 'Y') {
				callback_info("恢复成功", function() {
					$('#'+name+'_refresh_toolbar').click();
				});
			} else {
				error(result);
			}
		},
		error:function(){
			$.messager.progress('close');
		}
	});
}
