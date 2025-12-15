<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>

<div class="container narrow">
    <h2>회원가입</h2>

    <!-- 에러 메시지 -->
    <c:if test="${not empty error}">
        <div style="color:red; margin-bottom:10px;">
                ${error}
        </div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/auth/signup"
          enctype="multipart/form-data">

        <%--<div>
            <label>학교 이메일</label><br>
            <input type="email" name="loginId"
                   placeholder="happy@handong.ac.kr"
                   required>
        </div>--%>

        <div>
            <label>ID</label><br>
            <input type="text" name="loginId" required>
        </div>

        <div>
            <label>PASSWORD</label><br>
            <input type="password" name="password" required>
        </div>

        <div>
            <label>NAME</label><br>
            <input type="text" name="name" required>
        </div>

        <div>
            <label>한동대학교 소속임을 인증하세요!</label><br>
            <input type="file" name="studentCard"
                   accept="image/*"
                   required>
        </div>

        <div style="margin-top:15px;">
            <button type="submit">회원가입</button>
        </div>
    </form>

    <div style="margin-top:10px;">
        <a href="${pageContext.request.contextPath}/auth/login">
            Login Page
        </a>
    </div>
</div>

</body>
</html>
