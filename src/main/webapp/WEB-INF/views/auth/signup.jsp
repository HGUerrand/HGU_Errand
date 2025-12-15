<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>

<div class="auth-page">
    <div class="auth-card">
        <div class="auth-title">회원가입</div>

        <form class="auth-form" method="post"
              action="${pageContext.request.contextPath}/auth/signup"
              enctype="multipart/form-data">

            <input class="auth-input" type="text" name="loginId" placeholder="아이디" required>
            <input class="auth-input" type="password" name="password" placeholder="비밀번호" required>
            <input class="auth-input" type="text" name="name" placeholder="이름" required>

            <input class="auth-file" type="file" name="studentCard" accept="image/*" required>

            <button class="auth-btn" type="submit">회원가입</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인</a> ·
            <a href="${pageContext.request.contextPath}/">홈으로</a>
        </div>
    </div>
</div>

</body>
</html>
