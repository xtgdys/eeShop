<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
    response.sendRedirect(request.getContextPath() + "/product?method=index");
%>
</body>
</html>