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

        <div style="margin: 0 0 18px 0; display:flex; justify-content:center;">
            <div id="google-login"></div>
        </div>

        <!-- 에러 메시지 -->
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

<script src="https://accounts.google.com/gsi/client" async defer></script>
<script>
    window.onload = function () {
        if (!window.google || !google.accounts || !google.accounts.id) return;

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
        const credential = response && response.credential ? response.credential : "";
        if (!credential) {
            alert("Google credential이 없습니다.");
            return;
        }

        // ✅ JSON 말고 form-urlencoded로 전송 (Jackson 필요없음)
        const body = new URLSearchParams();
        body.append("credential", credential);

        fetch("${pageContext.request.contextPath}/auth/google", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
            credentials: "same-origin",
            body: body.toString()
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    location.href = "${pageContext.request.contextPath}/errand/list";
                } else {
                    alert(data.message || "Google 로그인 실패");
                }
            })
            .catch((e) => {
                console.error(e);
                alert("Google 로그인 실패");
            });
    }
</script>

</body>
</html>
