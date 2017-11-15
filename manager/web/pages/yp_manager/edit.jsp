<%@page import="java.util.List"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="com.jinhua.User"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.tools.UI"%>
<%@page import="com.jinhua.server.tools.UI_Op"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Entity yp_goods=(Entity)request.getAttribute("yp_goods");
	boolean hasYp_goods=yp_goods!=null&&yp_goods.getResultCount()>0;
	
	String type = request.getParameter("type");
	// 	String cust_name=user.getXX("gym");
	List<Map<String, Object>> gymList = null;
	//查询码表信息
	Entity yp_code = null;
	Connection conn = null;
	IDB db = new DBM();
	JSONArray data = new JSONArray();
	Map<String,Object> gyms = new HashMap<>();
	String id = "";
	try {
		conn = db.getConnection();
		conn.setAutoCommit(true);
		yp_code = new EntityImpl("yp_code", conn);
		int size = yp_code.executeQuery("select * from yp_code where barcode=?  and bartype=? and cust_name=?",
				new String[]{"goods_type", "pub", cust_name});
		if (size > 0) {
			String content = yp_code.getStringValue("content");
			if (content != null && content.length() > 0) {
				data = new JSONArray(content);
			}
		}
		
		Entity gs = new EntityImpl(conn);
		size = gs.executeQuery("select gym, gym_name from yp_gym where cust_name=?", new String[]{cust_name});
		if(size > 0) {
			gymList = gs.getValues();
			for(int i = 0; i < size; i++) {
				String gym = gs.getStringValue("gym", i);
				String gym_name = gs.getStringValue("gym_name", i);
				gyms.put(gym, gym_name);
			}
		}
		if ("edit".equals(type)|| "detail".equals(type)) {
			yp_code = new EntityImpl("yp_goods", conn);
			String sql = "select * from yp_goods where cust_name=? and good_no = ?";
			yp_code.executeQuery(sql, new String[]{cust_name,yp_goods.getStringValue("good_no")});
			List<Map<String, Object>> typeCodeList = yp_code.getValues();
				for (Map<String, Object> m2 : gymList) {
					String m2_gym = m2.get("gym").toString();
						for (Map<String, Object> m1 : typeCodeList) {
							String m1_gym = m1.get("gym").toString();
							if (m1_gym.equals(m2_gym)) {
								m2.put("checked", "checked");
								break;
							} else {
								m2.put("checked", "");
							}

						}
					}
				}
		
	} catch (Exception e) {
	} finally {
		db.freeConnection(conn);
	}
%>
<!DOCTYPE HTML>
<html>
 <head>
  <jsp:include page="/public/edit_base.jsp" />
  <script type="text/javascript">
    var entity = "yp_goods";
    var form_id = "yp_goodsFormObj";
    var lockId=new UUID();
    $(document).ready(function() {
		$('#yp_goods__good_type').combobox('loadData',<%=data%>);
		if("edit" == "<%=type%>") {
			//$("#yp_goods__good_no").attr("disabled", "disabled");
			$("#yp_goods__good_num").numberbox("disable", true);
// 			$("#yp_goods__good_num").numberbox({
// 				"disabled" : true
// 			});
		}else{
			setTimeout("$('#yp_goods__good_no').focus();",1000);
		}
    });
    function loadGood(v){
    	if(v!=""){
    		$.ajax({
    			url : "yp_goods_query_by_good_no",
    			type:"POST",
    			data : {
    				good_no:v
    			},
    			dataType: "json",
    			success: function(data) {
    				if(data.rs == "Y") {
    					var good = data.good.listData[0];
    					//console.log(good);
						$("#yp_goods__good_type").combobox("setValue",good.good_type);    					
						$("#yp_goods__store_id").combobox("setValue",good.store_id);    					
						$("#yp_goods__good_name").val(good.good_name);    	
						$("#number_text").text("添加数量");
						$("#yp_goods__emp_percent").numberbox("setValue",good.emp_percent);    					
						$("#yp_goods__price").numberbox("setValue",good.price/100);    					
						$("#yp_goods__bprice").numberbox("setValue",good.bprice/100);    					
						$("#yp_goods__warn_num").numberbox("setValue",good.warn_num);    					
						$("#_yp_goods__pic img").attr("src",good.pic + "?imageView2/1/w/50/h/50");		
						$("#yp_goods__pic").val(good.pic);    					
						$("#yp_goods__remark").val(good.remark); 
						var gyms = $("input[name=goods_gym]");
						for(var i = 0;i<gyms.length;i++){
							var g = gyms[i];
							if($(g).val() == good.gym){
							   $(g).attr("checked",true);
							}
						}
    				} else {
    					$("#number_text").text("库存数量");
    					var gyms = $("input[name=goods_gym]");
						for(var i = 0;i<gyms.length;i++){
							var g = gyms[i];
							$(g).removeAttr("disabled");
							$(g).attr("checked",false);
						}
    				}
    			},error: function() {
    			}
    		});
    	}
    }
  </script>
  
  <script type="text/javascript" charset="utf-8" src="pages/yp_goods/yp_goods.js"></script>
 </head>
<body>
	  <form class="l-form" id="yp_goodsFormObj" method="post">
	    <input id="yp_goods__id" name="yp_goods__id" type="hidden" value='<%=hasYp_goods?yp_goods.getStringValue("id"):""%>'/>
	    <input id="yp_goods__cust_name" name="yp_goods__cust_name" type="hidden" value='<%=hasYp_goods?yp_goods.getStringValue("cust_name"):""%>'/>
	    <input id="yp_goods__gym" name="yp_goods__gym" type="hidden" value='<%=hasYp_goods?yp_goods.getStringValue("gym"):""%>'/>
	    <ul>
	      <li style="width: 90px; text-align: left;">商品类型：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__good_type" name="yp_goods__good_type"
						class="easyui-combobox" style="width: 164px;" type="text"
						data-options="required:true,validType:'length[0,10]',valueField:'id',textField:'text',editable:false"
						value='<%=hasYp_goods ? yp_goods.getStringValue("good_type") : ""%>' />
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 90px; text-align: left;">仓库：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <%=UI.createSelectBySql("yp_goods__store_id","select id code, store_name note from yp_store where gym='" + user.getXX("gym") + "'" ,hasYp_goods?yp_goods.getStringValue("store_id"):"",true,"{'style':'width:164px'}") %>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">商品名称：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__good_name" name="yp_goods__good_name" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasYp_goods?yp_goods.getStringValue("good_name"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 90px; text-align: left;">商品条形码：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__good_no" oninput="loadGood(this.value)" name="yp_goods__good_no" class="easyui-validatebox"  style="width: 164px;" type="text" data-options="required:true,validType:'length[0,100]'" value='<%=hasYp_goods?yp_goods.getStringValue("good_no"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;" id="number_text">库存数量：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__good_num" name="yp_goods__good_num" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:true,min:0" value='<%=hasYp_goods?yp_goods.getStringValue("good_num"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 90px; text-align: left;">销售价：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__price" name="yp_goods__price" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:2,required:true" value='<%=hasYp_goods?yp_goods.getFloatValue("price") / 100:""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">进价：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__bprice" name="yp_goods__bprice" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:2,required:true" value='<%=hasYp_goods?yp_goods.getFloatValue("bprice") / 100:""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    <li style="width: 90px; text-align: left;">员工折扣：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__emp_percent" name="yp_goods__emp_percent" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:2,required:false,suffix:'%', min:0, max:100" value='<%=hasYp_goods?yp_goods.getFloatValue("emp_percent"):""%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">提醒数量：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <input id="yp_goods__warn_num" name="yp_goods__warn_num" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:false,min:0" value='<%=hasYp_goods?yp_goods.getStringValue("warn_num"):"0"%>'/>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      <li style="width: 90px; text-align: left;">商品图片：</li>
       <li style="width: 170px; text-align: left;">
	        <div class="l-text" style="width: 168px;">
	          <%-- <input id="yp_goods__pic" name="yp_goods__pic" class="easyui-numberbox" style="width: 164px;" type="text" data-options="precision:0,required:false,min:0" value='<%=hasYp_goods?yp_goods.getStringValue("warn_num"):"0"%>'/>
 --%>	          <input id="yp_goods__pic" name="yp_goods__pic" type="hidden" value="<%=hasYp_goods ? yp_goods.getStringValue("pic") : ""%>"><a href="javascript:uploadFile('yp_goods__pic','','');" class="btn btn-xs btn-default btn-block" style="width: 100px;">上传文件</a><div id="yp_goods__pic"><a href='<%= hasYp_goods && (!"".equals(yp_goods.getStringValue("pic"))) ? yp_goods.getStringValue("pic") : "javascript:void(0)" %>' target='_blank'><img src='<%=hasYp_goods ? yp_goods.getStringValue("pic") : "" %>?imageView2/1/w/50/h/50'></a></div>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	      </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">备注：</li>
       <li style="width: 470px; text-align: left;height: 50px;">
	        <div class="l-text" style="width: 468px;height:48px;">
	        <textarea  id="yp_goods__remark" name="yp_goods__remark" class="easyui-validatebox"style="width: 464px;height: 46px;" data-options="required:false,validType:'length[0,65000]'" ><%=hasYp_goods?yp_goods.getStringValue("remark"):""%></textarea>
	          <div class="l-text-l"></div>
	          <div class="l-text-r"></div>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	    <ul>
	      <li style="width: 90px; text-align: left;">可用门店：</li>
       <li style="width: 470px; text-align: left;height: 80px;">
	        <div class="l-text" style="width: 468px;height:78px; overflow: scroll;">
	        	 <%
					for (Map<String, Object> m : gymList) {
				%> 
				<input type="checkbox" <%if(!"add".equals(type)){ %> disabled="disabled"<%}%> name="goods_gym" value="<%=m.get("gym")%>" <%=m.get("checked")%> />
				<%=m.get("gym_name")%> 
				<%
        			}
 			 %>
	        </div>
	      </li>
	      <li style="width: 40px;"></li>
	    </ul>
	  </form>
 </body>
</html>