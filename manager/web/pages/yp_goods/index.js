$(document).ready(function() {
    $('#yp_goods_jh_process_leftpage').load("public/pub/code/index.jsp?c=goods_type&n=商品类型&t=pub&g=" + cust_name,
    function() {

        $("#code").css("width", "60%");
        $("#code").prev().css("width", "30%");
        $("#code").attr("placeholder", "分类代码");
        $("#code").prev().html("分类代码");

        $("#note").attr("placeholder", "分类名称");
        $("#note").prev().html("分类名称");
        $("#note").css("width", "60%");
        $("#note").prev().css("width", "30%");

        $('#code_tree').tree({
            onClick: function(node) {
                var gym_tree = $('#gym_tree').tree("getSelected");
                if (gym_tree == null) {
                    yp_goods___yp_goodsparams = {
                        sql: "select * from yp_goods where good_type=? and gym=?",
                        sqlPs: [node.id, gym]
                    };
                } else {
                    var gym_id = gym_tree.id;
                    yp_goods___yp_goodsparams = {
                        sql: "select * from yp_goods where good_type=? and gym=?",
                        sqlPs: [node.id, gym_id]
                    };
                }

                window.localStorage.setItem("leftParams", JSON.stringify(yp_goods___yp_goodsparams));
                cq('yp_goods___yp_goods', 'task', yp_goods___yp_goodsparams);
            }
        });
    });
    $('#yp_goods_jh_process_leftpage1').load("public/pub/areaCode/index.jsp?c=area_code&n=行政区域&t=pub&g=" + cust_name,
    function() {
        $('#gym_tree').tree({
            onClick: function(node) {
                var code_tree = $("#code_tree").tree("getSelected");
                if (code_tree == null) {
                    yp_goods___yp_goodsparams = {
                        sql: "select * from yp_goods where gym=?",
                        sqlPs: [node.id]
                    };
                } else {
                    var type = code_tree.id;
                    yp_goods___yp_goodsparams = {
                        sql: "select * from yp_goods where gym=? and good_type=?",
                        sqlPs: [node.id, type]
                    };
                }
                cq('yp_goods___yp_goods', 'task', yp_goods___yp_goodsparams);
            }
        });

        /*
										 * $('#gym_tree').tree({ onClick:
										 * function(node) {
										 * yp_goods___yp_goodsparams = { sql:
										 * "select * from yp_goods where gym=?
										 * and cust_name=? and good_type=?",
										 * sqlPs: [node.id,cust_name,type] };
										 * cq('yp_goods___yp_goods',
										 * 'task',yp_goods___yp_goodsparams); }
										 * });
										 */

    });
});
function storeManage() {
    art.dialog.open("pages/yp_store/index.jsp", {
        title: '仓库设置',
        width: 1300,
        height: 700,
        lock: true,
        cancelVal: "关闭",
        cancel: function() {
            return true;
        },
    });
}
// 调整商品价格为健身房特有价格
function goodsPriceModify() {

    var selected = getSelectedCount('yp_goods___yp_goods');
    if (selected == 1) {
        var good_id = getValuesByName('id', 'yp_goods___yp_goods');
        var good_name = getValuesByName('good_name', 'yp_goods___yp_goods');
        var gym = getValuesByName('gym__qm_code', 'yp_goods___yp_goods');
        // alert(id+" "+good_name);
        art.dialog.open("pages/yp_goods/yp_goods_price_edit.jsp?good_id=" + good_id + "&good_name=" + good_name + "&gym=" + gym, {
            title: '价格调整',
            width: 400,
            height: 100,
            lock: true,
            cancelVal: "关闭",
            cancel: function() {
                return true;
            },
            okValue: '确定',
            ok: function() {
                var iframe = this.iframe.contentWindow;
                var sform = iframe.document.getElementById("yp_goods_priceFormObj");

                var new_price = iframe.document.getElementById("yp_goods_price__new_price").value;
                var id = iframe.document.getElementById("yp_goods_price__id").value;
                var flag = false;
                if (new_price == "" || new_price < 0) {
                    return false;
                }
                if (new_price == 0) {
                    var f = confirm("设置为0则恢复默认价格,是否确认?");
                    if (!f) {
                        return false;
                    }
                }
                $.ajax({
                    url: "save_gym_goods_price",
                    data: {
                        id: id,
                        good_id: good_id,
                        new_price: new_price
                    },
                    async: false,
                    dataType: "json",
                    success: function(data) {
                        if (data.rs == 'Y') {
                            alert("设置成功");
                            flag = true;
                            // $("#yp_goods___yp_goods_table").datagrid("reload");
                        } else {
                            error("操作失败,请重试");
                            flag = false;
                        }
                    }
                });
                return flag;
            },
        });

    } else {
        error("请选择一条信息进行编辑!");
    }

}
// 可用门店单个商品价格统一调整
function gymsGoodsPriceModify() {
    var selected = getSelectedCount('yp_goods___yp_goods');
    if (selected == 1) {
        var good_id = getValuesByName('id', 'yp_goods___yp_goods');
        var good_name = getValuesByName('good_name', 'yp_goods___yp_goods');
        var gym = getValuesByName('gym__qm_code', 'yp_goods___yp_goods');
        var good_no = getValuesByName('good_no', 'yp_goods___yp_goods');
        var flag = false;
        // alert(id+" "+good_name);
        art.dialog.open("pages/yp_goods/yp_goods_price_edit_gym.jsp?good_id=" + good_id + "&good_name=" + good_name + "&gym=" + gym + "&good_no=" + good_no, {
            title: '可用门店商品价格调整(商品编号' + good_no + ')',
            width: 800,
            height: 400,
            lock: true,
            cancelVal: "关闭",
            cancel: function() {
                return true;
            },
            okValue: '确定',
            ok: function() {
                var iframe = this.iframe.contentWindow;
                var sform = iframe.document.getElementById("yp_goods_priceFormObj");
                var doc = $(iframe.document);
                var price_inputs = $(doc).find("input[name^='price']");
                var json = new Array();
                for (var i = 0; i < price_inputs.length; i++) {
                    var o = price_inputs[i];
                    var new_price = $(o).val();
                    var gym = $(o).attr("id");
                    json.push({
                        gym: gym,
                        new_price: new_price
                    });
                    if ($(o).val() == "" || $(o).val() <= 0) {
                        alert("修改的价格不能为空或小于0");
                        return false;
                    }
                }
                $.ajax({
                    url: "save_gym_goods_price_gyms",
                    data: {
                        json: JSON.stringify(json),
                        good_no: good_no
                    },
                    async: false,
                    dataType: "json",
                    success: function(data) {
                        if (data.rs == 'Y') {
                            alert("设置成功");
                            flag = true;
                            document.getElementById("yp_goods___yp_goods_refresh_toolbar").click();
                            // $("#yp_goods___yp_goods_table").datagrid("reload");
                        } else {
                            error("操作失败,请重试");
                            flag = false;
                        }
                    }
                });
                return flag;
            },
        });

    } else {
        error("请选择一条信息进行编辑!");
    }
}
function goodsCheck() {
    var selected = getSelectedCount('yp_goods___yp_goods');
    var abc = document.getElementById("yp_goods___yp_goods_refresh_toolbar")
    if (selected == 1) {
        var good_id = getValuesByName('id', 'yp_goods___yp_goods');
        var good_name = getValuesByName('good_name', 'yp_goods___yp_goods');
        art.dialog.open("pages/yp_goods/yp_goods_check.jsp?good_id=" + good_id, {
            title: '商品(' + good_name + ')盘点',
            width: 800,
            height: 400,
            lock: true,
            cancelVal: "关闭",
            cancel: function() {
                return true;
            },
            okValue: '确定',
            ok: function() {
                var iframe = this.iframe.contentWindow;
                iframe.save(this);
                document.getElementById("yp_goods___yp_goods_refresh_toolbar").click();
                return false;
            },
        });

    } else {
        error("请选择一条信息进行编辑!");
    }
}
var code_length = 0;
function goodsPutin(){
	var text = "商品条码:<input type='text' id='good_code'/>" +
			"<br>操作数量:<input type='number' id='goodsPutinNumber'>"+"<br>商品进价:<input type='number' id='goodsInprice'>"+
			"<br>操作备注:<select style='width:156px' id='remark'><option value='商品进货入库' selected>商品进货入库</option><option value='商品退货出库'>商品退货出库</option></select>";
	
	art.dialog({
        title: "商品出入库(不存在的条形码会跳转到添加页面)",
        content: text,
        lock: true,
        ok: function() {
        	var good_code = $("#good_code").val();
        	var goodsPutinNumber = $("#goodsPutinNumber").val();
        	var goodsInprice = $("#goodsInprice").val();
        	var remark = $("#remark").val();
        	var flag = false;
        	if(goodsPutinNumber == 0){
        		error("不能是0哦");
        		return false;
        	}
        		$.ajax({
        			url:"query_good_by_goodNo_andPutin",
        			type:"post",
        			dataType:"json",
        			async:false,
        			timeout:10000,
        			data:{good_no:good_code,
        				goodsPutinNumber:goodsPutinNumber,
        				goodsInprice:goodsInprice,
        				remark:remark
        			},
        			success:function(data){
        				if(data.rs == "Y"){
        					alert("操作成功");
        					document.getElementById('yp_goods___yp_goods_refresh_toolbar').click();
        					/*var win = window.document.getElementsByName("Openuser_info_dialog");
        					if(win!= null&& win.length>0){
        						win[0].contentWindow.location.reload(true);
        					}*/
        					flag = true;
        				}else if(data.rs =="N"){
        					add('yp_goods___yp_goods');
        					flag =  true;
        				}else{
        					error(data.rs);
        				}
        			}
        		});
        		return flag;
        },
        cancelVal: "关闭",
        cancel: function() {
            return true;
        },
        okValue: "确定",
        init:function(){
        	$("#good_code").focus();
        }     
    }); 
}
