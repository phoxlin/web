<%@page import="com.jinhua.server.HtmlServlet"%>
<%@page import="com.jinhua.server.tools.Resources"%>
<%
	Resources.init();
	HtmlServlet.fileBuffer.clear();
	HtmlServlet.fileInfos.clear();
	out.println("ok....");
%>