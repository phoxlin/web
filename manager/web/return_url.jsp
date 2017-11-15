
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%
	/* *
	 功能：支付宝页面跳转同步通知页面
	 版本：3.2
	 日期：2011-03-17
	 说明：
	 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
	 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
	 //***********页面功能说明***********
	 该页面可在本机电脑测试
	 可放入HTML等美化页面的代码、商户业务逻辑程序代码
	 TRADE_FINISHED(表示交易已经成功结束，并不能再对该交易做后续操作);
	 TRADE_SUCCESS(表示交易已经成功结束，可以对该交易做后续操作，如：分润、退款等);
	 //********************************
	 * */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="com.alipay.config.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支付宝页面跳转同步通知页面</title>
<script type='text/javascript' src='app/js/zepto.min.js' charset='utf-8'></script>
<script type="text/javascript" src="public/js/jinhua-yun-1.0.0.js"></script>
</head>
<body>
	<%
		//获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			System.out.println(name);
			String valueStr = request.getParameter(name);
			if(name.equals("sign")){
				valueStr=valueStr.replace(" ", "+");
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// 			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			// 			valueStr = URLEncoder.encode(valueStr, "UTF-8");
			params.put(name, valueStr);
		}

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		//支付宝交易号

		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
	%>
	<script type="text/javascript">
	var cust_name = store.get('cust_name');
	var user_id = store.get('user_id');
	$.ajax({
		url : "App_alipay",
		type : "POST",
		data : {
			cust_name : cust_name,
			user_id : user_id,
			out_trade_no : '<%=out_trade_no%>',
			trade_no:'<%=trade_no%>'
			},
			dataType : "json",
			success : function(data) {
				if (data.rs == "Y") {
					// 				alert("登录成功!");
					// 				location.href="app/user_index.jsp?cust_name="+cust_name;
				} else {
					alert(data.rs);
				}
			}
		})
	</script>
</body>


</html>