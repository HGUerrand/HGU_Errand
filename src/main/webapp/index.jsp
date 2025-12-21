<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>HGU Errand</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/index_1.css">
</head>
<body>

<!-- HERO -->
<section class="hero-section">
    <div class="hero-content">
        <h1>Handong Errand</h1>
        <p>
            한동대학교 구성원을 위한<br>
            <strong>심부름 요청 & 매칭 플랫폼</strong>
        </p>
        <a href="#intro" class="scroll-down">↓ 더 알아보기</a>
    </div>
</section>

<!-- INTRO -->
<section class="intro-section" id="intro">
    <div class="intro-text">
        <h2>캠퍼스 안에서,<br>더 빠르고 더 안전하게</h2>
        <p>
            학생 인증 기반으로 운영되는<br>
            한동대학교 전용 심부름 서비스입니다.
        </p>
    </div>
</section>

<!-- FEATURES -->
<section class="feature-section">
    <div class="feature">
        <h3>🎓 학생 인증</h3>
        <p>
            학생증 업로드 및 승인 절차로<br>
            믿을 수 있는 학내 사용자만 이용 가능
        </p>
    </div>

    <div class="feature">
        <h3>📦 심부름 매칭</h3>
        <p>
            소소한 부탁부터 급한 요청까지<br>
            필요한 심부름을 바로 등록
        </p>
    </div>

    <div class="feature">
        <h3>💬 실시간 채팅</h3>
        <p>
            요청자와 수행자가 즉시 소통하며<br>
            빠르고 정확한 거래 가능
        </p>
    </div>
</section>

<!-- ABOUT -->
<section class="about-section">
    <h2>누가 만들었나요?</h2>
    <p class="about-desc">
        웹 서비스 개발 수업 프로젝트로 제작된 서비스입니다.
    </p>

    <div class="creator-box">
        <div>
            <strong>22400254 박나림</strong>
        </div>
        <div>
            <strong>22400661 정다연</strong>
        </div>
    </div>
</section>

<!-- CTA -->
<section class="cta-section">
    <h2>지금 시작해보세요</h2>
    <p>한동 구성원이라면 누구나 참여할 수 있습니다.</p>

    <div class="cta-buttons">
        <a class="btn primary"
           href="${pageContext.request.contextPath}/auth/signup">회원가입</a>
        <a class="btn"
           href="${pageContext.request.contextPath}/auth/login">로그인</a>
    </div>
</section>

</body>
</html>
