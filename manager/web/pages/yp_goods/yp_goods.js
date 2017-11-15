function savaAddDialog(win,doc,entity,name){
	$.messager.progress();
	$('#yp_goodsFormObj').form('submit',	{
		url : "yp_goods-add?e=" + entity+"&lockId="+lockId,
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

function savaEditDialog(win,doc,entity,name){
	
	$.messager.progress();
	$('#' + form_id).form('submit',	{
		url : "yp_goods-edit?e=" + entity+"&lockId="+lockId,
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

var content = '<form class="form-inline" id="goods_trans_form" style="width:600px;height:300px;">\
	<input type="hidden" id="goods_id" style="width:45%" class="form-control" readonly="readonly" placeholder=""/>  \
	<div class="row">\
		<div class="col-xs-10">\
			<div class="form-group" style="display: block;">  \
				<label style="width:25%" class="input-sm" data-onlinelabel="N" for="userAdd-phone">商品名称</label>\
				<input type="text" id="goods_name" style="width:45%" class="form-control" readonly="readonly" placeholder=""/>  \
			</div>\
		</div>\
	</div>\
	<div class="row">\
		<div class="col-xs-10">\
			<div class="form-group" style="display: block;">\
				<label style="width:25%" class="input-sm" data-onlinelabel="N" for="userAdd-phone">当前仓库</label>\
				<input type="text" id="store_name" style="width:45%" class="form-control" readonly="readonly" placeholder=""/>\
			</div>\
		</div>\
	</div>\
	<div class="row">\
		<div class="col-xs-10">\
			<div class="form-group" style="display: block;">    \
				<label style="width:25%" class="input-sm" data-onlinelabel="N" for="userAdd-phone">当前数量</label>\
				<input type="number" id="store_num" style="width:45%" class="form-control" readonly="readonly" placeholder=""/>\
			</div>\
		</div>\
	</div>\
	<div class="row">\
		<div class="col-xs-10">\
			<div class="form-group" style="display: block;">    \
				<label style="width:25%" class="input-sm" data-onlinelabel="N" for="tab3_card">目标仓库</label>\
				<select id="trans_store" name="trans_store" style="width:45%" class="form-control">\
				</select>\
			</div>\
		</div>\
	</div>\
	<div class="row">\
		<div class="col-xs-10">\
			<div class="form-group" style="display: block;">    \
				<label style="width:25%" class="input-sm" data-onlinelabel="N" for="userAdd-phone">转移数量</label>\
				<input type="number" id="trans_num" name="trans_num" style="width:45%" class="form-control" placeholder="转移数量"/>\
			</div>\
		</div>\
	</div>\
	<div class="row">\
		<div class="col-xs-5">\
			<div class="form-group" style="display: block;text-align: right;margin-top:15px;">\
				<button type="button" class="btn btn-custom" style="width:35%;" onclick="saveTrans()">保存</button>\
			</div>\
		</div>\
	</div>\
</form>\
</script>';


function toOptions(ob) {
	var h = ""
	for(var i in ob) {
		h += '<option value="'+i+'">'+ob[i]+'</option>';
	}
	return h;
}
function goodsTrans() {
	var selected = getSelectedCount('yp_goods___yp_goods');
	if (selected == 1) {
		var id = getValuesByName('id', 'yp_goods___yp_goods');
		$.ajax({
			url : "yp_store-get",
			type: "POST",
			data : {
				gid : id
			},
			dataType: "json",
			success: function(data) {
				if(data.rs == "Y") {
					var g = data.goods;
					var stores = data.stores;
					art.dialog({
						title: "商品调动",
						content : content,
						init : function() {
							$("#goods_id").val(g.id);
							$("#goods_name").val(g.good_name);
							$("#store_name").val(g.store_name);
							$("#store_num").val(g.good_num);
							
							$("#trans_store").html(toOptions(stores));
						}
					})	
				} else {
					error(data.rs);
				}
			},
			error : function() {
				error("网络错误,请重试!");
			}
		})
	} else {
		error("请选择一条信息进行编辑!");
	}
}

function saveTrans() {
	var gid = $("#goods_id").val();
	var toStore = $("#trans_store").val();
	var num = $("#trans_num").val();
	
	if(!toStore) {
		error("请选择转移的仓库!");
		return;
	}
	
	num = Number(num);
	if(isNaN(num) || num <= 0 ) {
		error("请填写正确的商品数量!");
		return;
	} else {
		num = Math.floor(num);
		$("#trans_num").val(num);
	}
	
	$.ajax({
		url : "yp_goods-trans",
		type:"POST",
		data : {
			gid : gid,
			toStore: toStore,
			num : num
		},
		dataType: "json",
		success: function(data) {
			if(data.rs == "Y") {
				callback_info("转移成功", function() {
					location.reload(); 
				})
			} else {
				error(data.rs);
			}
		},error: function() {
			error("网络错误,请重试!");
		}
	})
}