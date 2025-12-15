<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>

<div class="auth-page">
    <div class="auth-card">
        <div class="auth-title">로그인</div>

        <form class="auth-form" method="post"
              action="${pageContext.request.contextPath}/auth/login">

            <input class="auth-input" type="text" name="loginId" placeholder="아이디" required>
            <input class="auth-input" type="password" name="password" placeholder="비밀번호" required>

            <button class="auth-btn" type="submit">LOGIN</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/signup">회원가입</a> ·
            <a href="${pageContext.request.contextPath}/">홈으로</a>
        </div>
    </div>
</div>

</body>
</html>
