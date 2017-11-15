<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.gd.m.Shop"%>
<%@page import="com.gd.m.Cust"%>
<%@page import="com.gd.m.GdUser"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	GdUser user=(GdUser)SystemUtils.getSessionUser(request, response);
	if(user==null || !user.getCD().contains("GOODS_INFO")){ request.getRequestDispatcher("/").forward(request, response);}
	String cust_name = user.getCust_name();
	
	String taskcode = "yp_goods";
	String taskname = "商品管理";
	
	String sId = request.getParameter("sid");
	List<String> gymlist = user.getGymList();
	
%>
<!DOCTYPE html>
<html>
<head>
<title><%=taskname%></title>
<jsp:include page="/public/base.jsp" />
<script type="text/javascript">

  var gym = '<%=user.getGym()%>';
//data-grid配置开始
///////////////////////////////////////////(1).yp_goods___yp_goods开始///////////////////////////////////////////
	//搜索配置
	var yp_goods___yp_goods_filter=[
{"rownum":2,"compare":"like","colnum":1,"label":"区域","type":"text","columnname":"areacode","bindType":"sql","bindData":"select id code,gym_name note from yp_gym where cust_name='1' ","ignore":"Y"},
{"rownum":2,"compare":"like","colnum":2,"bindType":"sql","bindData":"select id code,gym_name note from yp_gym where cust_name='1' ","label":"门店","type":"text","columnname":"gym"},
{"rownum":2,"compare":"=","colnum":3,"bindType":"sql","label":"仓库","type":"text","columnname":"store_id","bindData":"select id code, store_name note from yp_store where gym='<%=user.getXX("gym")%>'"},
{"rownum":2,"compare":"like","colnum":4,"label":"商品名称","type":"text","columnname":"good_name"},
{"rownum":2,"compare":"like","colnum":5,"label":"商品条形码","type":"text","columnname":"good_no"}
				      	 ];
	//编辑页面弹框标题配置
	var yp_goods___yp_goods_dialog_title='商品管理';
	//编辑页面弹框宽度配置
	var yp_goods___yp_goods_dialog_width=750;
	//编辑页面弹框高度配置
	var yp_goods___yp_goods_dialog_height=300;
	//IndexGrid数据加载提示配置
	var yp_goods___yp_goods_loading=true;
	//编辑页面弹框宽度配置
	var yp_goods___yp_goods_entity="yp_goods";
	//编辑页面路径配置
	var yp_goods___yp_goods_nextpage="pages/yp_goods/yp_goods_edit.jsp";
///////////////////////////////////////////(1).yp_goods___yp_goods结束///////////////////////////////////////////
    <%
    String sql="select a.* from yp_goods a";
    String par="";
    String where="";
    Cust cust = user.getCust();
	List<Shop> shops = cust.getShops();
	List<String> ss = new ArrayList<String>();
	if (shops.size() > 0) {
		int i = 0;
		for (Shop shop : shops) {
			String gym = shop.getGym();
			ss.add(gym);
		}

		where = " where a.gym in (" + Utils.getListString("?", ss.size()) + ")";
		par = "'" + Utils.getListString(ss, "','") + "'";
	} else {
		where = " where a.gym=?";
		par = "'" + user.getGym() + "'";
	}
    %>
    var cust_name='<%=user.getCust_name()%>';
    var yp_goods___yp_goods_params={sql:"<%=sql + where%>",sqlPs:[<%=par%>],cust_name:'<%=cust_name%>'};
//data-grid配置结束

</script>
<script type="text/javascript" charset="utf-8" src="pages/yp_goods/index.js"></script>
<script type="text/javascript" charset="utf-8" src="pages/yp_goods/yp_goods.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		 showTaskView('<%=taskcode%>','<%=sId%>','N');
		$.ajax({
			url : "goods-warn-info",
			type: "POST",
			dataType:"json",
			success: function(data) {
				if(data.rs == "Y" && data.warn_info) {
					error(data.warn_info);
				}
			}
		})
		
	});
	function yp_goods___yp_goodsHook(){
		areaGymSelect("yp_goods___yp_goods_areacode_search_0","yp_goods___yp_goods_gym_search_1");
	}
	
	//商品类型
	function goodsTypes(){
		art.dialog.open("public/pub/code/index.jsp?c=goods_type&n=商品类型&t=pub&g=<%=cust_name%>", {
	        title: '商品类型管理',
	        width: 500,
	        height: 800,
	        lock: true,
	        cancelVal: "关闭",
	        cancel: function() {
	            return true;
	        }
	    });
	}
</script>
</head>
<body>
	<div class="widget">
		<jsp:include page="/public/header.jsp">
			<jsp:param value="<%=taskname %>" name="view"/>
		</jsp:include>
		<div class="container-fluid">
			<div class="main main2">
				<div class="row">
					<div class="col-lg-12 col-md-12 col-xs-12">
						<div id="<%=taskcode%>_jh_process_page"> </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>