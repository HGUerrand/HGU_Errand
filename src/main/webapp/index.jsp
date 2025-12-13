<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>

<a href="<%= request.getContextPath() %>/errand/list"
   style="display:inline-block; padding:12px 16px; background:#111827; color:white;
          border-radius:10px; text-decoration:none; font-weight:600;">
    go to list →
</a>
</body>
</html>