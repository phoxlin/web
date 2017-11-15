//保存码表内容
function saveCode(id){
	var note = $("#note").val();
	if(note == null || note.length <= 0){
		$("#note").focus();
		return false;
	}
	var isNew = $("#isNew").val();
	$.ajax({
		type : "POST",
		url : "add-code",
		dataType : "json",
		data : {
			id: id,
			code: $("#code").val(),
			note: note,
			isNew: isNew
		},
		success : function(data) {
			var rs = data.rs;
			if("Y" == rs){
				callback_info("保存成功",function(){
					initInput();
					reloadTree(id);
				});
			}else{
				error(rs);
			}
		}
	});
}

//重新加载tree数据
function reloadTree(id){
	$.ajax({
		type : "POST",
		url : "get-codes",
		dataType : "json",
		data : {
			id: id
		},
		success : function(data) {
			var rs = data.rs;
			if("Y" == rs){
				$("#code_tree").tree("loadData", data.data);
			}else{
				error(rs);
			}
		}
	});
}

//删除码表内容
function delCode(id){
	var code = $("#note").val();
	if(code == null || code.length <= 0){
		error("请选择一条数据进行操作");
		return false;
	}
	art.dialog({
		title : '提示',
		content : '你确定要删除选择的信息吗?',
		icon : 'question',
		lock : true,
		okVal : "确定",
		ok : function() {
			$.ajax({
				type : "POST",
				url : "del-code",
				dataType : "json",
				data : {
					id: id,
					code: code
				},
				success : function(data) {
					var rs = data.rs;
					if("Y" == rs){
						callback_info("删除成功",function(){
							initInput();
							var node = $('#code_tree').tree('getSelected');
							if(node){
								$("#code_tree").tree("remove", node.target);
							}
						});
					}else{
						error(rs);
					}
				}
			});
		},
		cancelVal : "取消",
		cancel : function() {
			return true;
		}
	});
	
}

//初始化输入框
function initInput(){
	$("#code").val("");
	$("#note").val("");
	$("#code").removeAttr("disabled");
	$("#isNew").val("Y");
}