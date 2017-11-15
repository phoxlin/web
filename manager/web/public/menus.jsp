<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String view = request.getParameter("view");
	String model = request.getParameter("model");
%>
<div class="bread">
	<div class="row" style="line-height: 2;">
		<div class="col-md-9" style="padding-left: 0;">
			 <span class="position">
				 <img src="public/images/main/position.png"/>
				 <span>
				 	<a href="javascript: history.go(-1);"><%=model %></a> &gt; 
				 	<%=view %>
				 </span>
			 </span>
		</div>
		<div class="col-md-3" style="text-align: right;padding-right: 0;">
			<a  class="back" href="javascript: history.go(-1);">返回</a>
		</div>
	</div>
</div>