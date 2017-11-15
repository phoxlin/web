
function add(name) {
	addDialog(name);
}

function edit(name) {
	updateDialog(name);
}

function detail(name) {
	detailDialog(name);
}

// 删除
function del(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	
	var my_title="";
	try{
		if (typeof (dialog_title) == "undefined") {
			my_title=eval(name+'_dialog_title');
		}else{
			my_title=dialog_title;
		}
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
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var id = getValuesByName('_id', name);
	$.ajax({
		type : 'POST',
		url : "msLoad-del/" + name,
		dataType : 'json',
		data : {
			id : id + ""
		},
		success : function(data) {
			var result = "当前系统繁忙";
			result = data.rs;
			if (result == 'Y') {
				callback_info("删除成功啦", function() {
					$('#'+name+'_refresh_toolbar').click();
				});
			}
		}
	});
}

//添加
function addDialog(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	
	var my_title="";
	try{
		if (typeof (dialog_title) == "undefined") {
			my_title=eval(name+'_dialog_title');
		}else{
			my_title=dialog_title;
		}
	}catch(e){
	}
	var myWidth=800;
	try{
		if (typeof (dialog_width) == "undefined") {
			myWidth=eval(name+'_dialog_width');
		}else{
			myWidth=dialog_width;
		}
	}catch(e){
	}
	var myHeight=600;
	try{
		if (typeof (dialog_height) == "undefined") {
			myHeight=eval(name+'_dialog_height');
		}else{
			myHeight=dialog_height;
		}
	}catch(e){
	}
	
	
	var cq_type=$('#'+name+'_cq_type').val();
	var cq_editpage=$('#'+name+'_editpage').val();
	
	if(cq_editpage!=null&&cq_editpage!='undefined'&&cq_editpage.length>0){
		var url='msLoad-openAddDialog/' + name + '/null';
		if('OLDQM'==cq_type){
			url='qmLoad-openDetailDialog/' + name + '/null';
		}
		art.dialog.open(cq_editpage + "?type=add", {
			title : '添加' + my_title,
			width : myWidth,
			height : myHeight,
			okVal : "保存",
			ok : function() {
				var iframe = this.iframe.contentWindow;
				iframe.saveAddDialog(this, document);
				return false;
			}
		});
	}else{
		$.ajax({
			type : 'POST',
			url : 'msLoad-openAddDialog/' + name + '/null',
			dataType : 'json',
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					var list = data.list;
					var columnNumber = data.columnNumber;
					var msDetailTpl = document
							.getElementById('msDetailTpl').innerHTML;
					var content = template(msDetailTpl, {
						list : data.list,
						name : name,
						columnNumber : columnNumber,
						width : (myWidth - 50),
						height : (myHeight - 40)
					});

					art.dialog({
						title : '添加' + my_title,
						width : myWidth,
						height : myHeight,
						content : content,
						init : function() {
							$.parser.parse('#' + name + '_Form');
							addDialogHook();

						},
						lock : true,
						ok : function() {
							saveOrUpdate(data, name);
							return false;
						}
					});

				}
			}
		});
	}

}


function addDialogHook() {
}
function updateDialogHook() {
};


// 详细信息
function detailDialog(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	var selected = getSelectedCount(name);
	if (selected == 1) {
		var id = getValuesByName('_id', name);
		$
				.ajax({
					type : 'POST',
					url : 'msLoad-openDetailDialog/' + name + '/' + id,
					dataType : 'json',
					success : function(data) {
						var result = "当前系统繁忙";
						result = data.rs;
						if (result == 'Y') {
							var list = data.list;
							var columnNumber = data.columnNumber;
							var msDetailTpl = document
									.getElementById('msDetailTpl').innerHTML;
							var content = template(msDetailTpl, {
								list : data.list,
								name : name,
								columnNumber : columnNumber,
								width : (dialog_width - 50),
								height : (dialog_height - 40)
							});

							art.dialog({
								title : dialog_title + '详情',
								width : dialog_width,
								height : dialog_height,
								content : content,
								lock : true,
								cancelVal : "关闭",
								cancel : function() {
									return true;
								}
							});
						}
					}
				});
	} else {
		error("请选择一条数据进行操作");
	}
}

function onDblClick(name, index) {

}
//修改
function updateDialog(name) {
	if (typeof (name) == "undefined") {
		name = ms_name;
	}
	
	var my_title="";
	try{
		if (typeof (dialog_title) == "undefined") {
			my_title=eval(name+'_dialog_title');
		}else{
			my_title=dialog_title;
		}
	}catch(e){
	}
	var myWidth=800;
	try{
		if (typeof (dialog_width) == "undefined") {
			myWidth=eval(name+'_dialog_width');
		}else{
			myWidth=dialog_width;
		}
	}catch(e){
	}
	var myHeight=600;
	try{
		if (typeof (dialog_height) == "undefined") {
			myHeight=eval(name+'_dialog_height');
		}else{
			myHeight=dialog_height;
		}
	}catch(e){
	}
	
	var selected = getSelectedCount(name);
	if (selected == 1) {
		var id = getValuesByName('_id', name);
		
		var cq_type=$('#'+name+'_cq_type').val();
		var cq_editpage=$('#'+name+'_editpage').val();
		
		if(cq_editpage!=null&&cq_editpage!='undefined'&&cq_editpage.length>0){
			var url='msLoad-openAddDialog/' + name + '/'+id;
			if('OLDQM'==cq_type){
				url='qmLoad-openDetailDialog/' + name + '/'+id;
			}
			art.dialog.open(url, {
				title : '修改' + my_title,
				width : myWidth,
				height : myHeight,
				okVal : "保存",
				ok : function() {
					var iframe = this.iframe.contentWindow;
					iframe.saveAddDialog(this, document);
					return false;
				}
			});
		}else{
			$.ajax({	
				type : 'POST',
				url : "msLoad-openUpdateDialog/" + name + "/" + id,
				dataType : 'json',
				success : function(data) {
					var result = "当前系统繁忙";
					result = data.rs;
					if (result == 'Y') {
						var list = data.list;
						var columnNumber = data.columnNumber;
						var msDetailTpl = document
								.getElementById('msDetailTpl').innerHTML;
						var content = template(msDetailTpl, {
							list : data.list,
							name : name,
							columnNumber : columnNumber,
							width : (myWidth - 50),
							height : (myHeight - 40)
						});
						art.dialog({
							title : '修改' + my_title,
							width : myWidth,
							height : myHeight,
							content : content,
							init : function() {
								$.parser
										.parse('#' + name + '_Form');
								var width = $(
										'div[data-content-row="Y"]')
										.width() - 90;

								var onlineLabels = $('label[data-onlinelabel="Y"]');
								if (onlineLabels.length > 0) {
									var labelWidth = $(
											'label[data-onlinelabel="N"]')
											.width();
									onlineLabels.width(labelWidth);
									$('[data-onlineValue="Y"]')
											.width(
													width
															- labelWidth);
								}
								updateDialogHook(data.list);
							},
							lock : true,
							ok : function() {
								var flag = saveOrUpdate();
								return flag;
							}
						});
					}
				}
			});	
		}
	} else {
		error("请选择一条数据进行操作");
	}
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
