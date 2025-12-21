<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>

<div class="auth-page">
    <div class="auth-card">
        <div class="auth-title">로그인</div>

        <c:if test="${not empty errorMsg}">
            <div class="auth-error">
                    ${errorMsg}
            </div>
        </c:if>

        <form class="auth-form"
              method="post"
              action="${pageContext.request.contextPath}/auth/login">

            <input class="auth-input"
                   type="text"
                   name="loginId"
                   placeholder="아이디"
                   required>

            <input class="auth-input"
                   type="password"
                   name="password"
                   placeholder="비밀번호"
                   required>

            <button class="auth-btn" type="submit">LOGIN</button>
        </form>
    </div>
</div>

</body>
</html>
