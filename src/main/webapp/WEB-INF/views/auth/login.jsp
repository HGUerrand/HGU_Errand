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

        <!-- 🔥 Google Login -->
        <div style="margin-bottom:20px; text-align:center;">
            <div id="google-login"></div>
        </div>

        <!-- 🔥 에러 메시지 -->
        <c:if test="${not empty errorMsg}">
            <div class="auth-error">
                    ${errorMsg}
            </div>
        </c:if>

        <!-- 기존 로그인 -->
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

<!-- Google Script -->
<script src="https://accounts.google.com/gsi/client" async defer></script>
<script>
    window.onload = function () {
        google.accounts.id.initialize({
            client_id: "936294527684-ppn525f19crtp39da5te6kl73q2p19gs.apps.googleusercontent.com",
            callback: handleGoogleLogin
        });

        google.accounts.id.renderButton(
            document.getElementById("google-login"),
            { theme: "outline", size: "large", width: 260 }
        );
    };

    function handleGoogleLogin(response) {
        fetch("${pageContext.request.contextPath}/auth/google", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ credential: response.credential })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    location.href = "${pageContext.request.contextPath}/errand/list";
                } else {
                    alert(data.message);
                }
            })
            .catch(() => alert("Google 로그인 실패"));
    }
</script>

</body>
</html>
