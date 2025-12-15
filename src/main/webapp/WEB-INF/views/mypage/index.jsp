<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>My Page - HGU Errand</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-list.css">
</head>
<body>
<div class="container">

    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">마이페이지</p>
                <p class="subtitle">내 정보 + 내가 올린 게시글 🎄</p>
            </div>
        </div>
        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/list">목록</a>
            <a class="btn" href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
        </div>
    </div>

    <!-- 프로필 카드(일단 DB 컬럼 없으니 name만) -->
    <div class="card" style="margin-bottom:16px;">
        <div style="display:flex; align-items:center; gap:12px;">
            <div style="width:48px; height:48px; border-radius:50%; background:#eee;"></div>
            <div>
                <div style="font-weight:1000; font-size:16px;">
                    <c:out value="${me.name}"/>
                </div>
                <div style="color:var(--muted); font-size:12px; font-weight:800;">
                    <c:out value="${me.login_id}"/>
                </div>
            </div>
        </div>
        <div style="margin-top:12px; color:var(--muted); font-size:12px; font-weight:800;">
            (다음 단계) 프사/닉네임 수정 기능 연결 예정
        </div>
    </div>

    <h3 style="margin:10px 0 12px; font-size:16px;">내가 올린 게시글</h3>

    <div class="errand-grid">
        <c:forEach var="e" items="${myErrands}">
            <a class="card ${e.status eq '완료' ? 'doneCard' : ''}"
               href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">
                <div class="topRight">
                    <span class="reward">₩ ${e.reward}</span>
                    <c:choose>
                        <c:when test="${e.status eq '예약'}"><span class="statePill reserved">예약</span></c:when>
                        <c:when test="${e.status eq '완료'}"><span class="statePill done">완료</span></c:when>
                    </c:choose>
                </div>
                <h3>${e.title}</h3>
                <div class="row" style="margin-top:6px;">
                    <span style="font-size:12px;color:var(--muted);font-weight:900;">${e.time}</span>
                </div>
                <div class="meta">
                    <div>From: <b>${e.from}</b></div>
                    <div>To: <b>${e.to}</b></div>
                </div>
                <div class="footer"><span>${e.createdAt}</span></div>
            </a>
        </c:forEach>
    </div>

</div>
</body>
</html>