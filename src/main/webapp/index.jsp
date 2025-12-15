<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Handong Errand</title>

    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/common.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/login.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/index.css">
</head>
<body>

<div class="container narrow index-wrap">

    <h1 class="index-title">Handong Errand</h1>
    <p class="index-subtitle">
        한동대학교 구성원을 위한<br>
        심부름 요청 & 매칭 플랫폼
    </p>

    <div class="index-features">
        <div class="feature-box">
            <h3>🎓 학생증 인증</h3>
            <p>학생증 업로드와 관리자 승인으로<br>안전한 학내 서비스</p>
        </div>

        <div class="feature-box">
            <h3>💬 실시간 채팅</h3>
            <p>요청자와 수행자가<br>즉시 소통 가능</p>
        </div>

        <div class="feature-box">
            <h3>📦 심부름 매칭</h3>
            <p>소소한 부탁부터<br>긴급한 요청까지</p>
        </div>
    </div>

    <div class="index-buttons">
        <a class="btn btn-primary"
           href="<%=request.getContextPath()%>/auth/signup">
            회원가입
        </a>
        <a class="btn btn-outline"
           href="<%=request.getContextPath()%>/auth/login">
            로그인
        </a>
    </div>

</div>

</body>
</html>
