<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>客户列表</title>
</head>
<body>
  <h1>客户列表</h1>
  <table>
    <tr>
      <th>客户名称</th>
      <th>客户联系人</th>
      <th>联系电话</th>
      <th>邮箱</th>
      <th>操作</th>
    </tr>
    <c:forEach var="customer" items="customers">
      <tr>
        <td>${customer.name}</td>
        <td>${customer.contact}</td>
        <td>${customer.telephone}</td>
        <td>${customer.email}</td>
        <td><a href="#">编辑</a><a href="#">删除</a></td>
      </tr>
    </c:forEach>

  </table>

</body>
</html>
