<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>HGU Errand</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/index.css">
</head>
<body>

<div class="container">

    <div class="hero">
        <h1>Handong Errand</h1>
        <p>한동대학교 구성원을 위한<br>심부름 요청 & 매칭 플랫폼</p>
    </div>

    <div class="feature-grid">
        <div class="feature-card">
            <h3>🎓 학생 인증</h3>
            <p>학생증 업로드와 승인으로<br>안전한 학내 서비스</p>
        </div>

        <div class="feature-card">
            <h3>💬 실시간 채팅</h3>
            <p>요청자와 수행자가<br>즉시 소통 가능</p>
        </div>

        <div class="feature-card">
            <h3>📦 심부름 매칭</h3>
            <p>소소한 부탁부터<br>급한 요청까지</p>
        </div>
    </div>

    <div class="hero-actions">
        <a class="btn primary"
           href="${pageContext.request.contextPath}/auth/signup">회원가입</a>
        <a class="btn"
           href="${pageContext.request.contextPath}/auth/login">로그인</a>
    </div>

</div>

</body>
</html>
