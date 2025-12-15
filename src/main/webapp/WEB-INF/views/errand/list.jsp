<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>HGU Errand - List</title>

    <!-- 공통 + 리스트 CSS -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-list.css">
</head>

<body>
<div class="container">

    <!-- Top bar -->
    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">HGU Errand</p>
                <p class="subtitle">심부름 빠르게 매칭 🎄</p>
            </div>
        </div>

        <div class="actions">
            <div class="actions">
                <a class="btn" href="<%=request.getContextPath()%>/">홈</a>

                <!-- 채팅 목록 -->
                <a class="btn" href="<%=request.getContextPath()%>/chat/list">채팅</a>

                <!-- 마이페이지 -->
                <a class="btn" href="<%=request.getContextPath()%>/mypage">마이</a>
            </div>
        </div>
    </div>

    <!-- Search (UI only) -->
    <div class="searchbar">
        <span style="font-weight:1000;color:var(--muted);">🔎</span>
        <input placeholder="제목/장소로 검색 (UI만 먼저)" />
        <span class="chip active">전체</span>
    </div>

    <!-- Filters (UI only) -->
    <div class="filters">
        <span class="chip active">모집중</span>
        <span class="chip">마감임박</span>
        <span class="chip">심부름</span>
        <span class="chip">대리구매</span>
        <span class="chip">픽업</span>
    </div>

    <!-- Grid -->
    <div class="errand-grid">
        <c:forEach var="e" items="${errands}">
            <a class="card ${e.status eq '완료' ? 'doneCard' : ''}"
               href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">

                <!-- 상단: 가격 + 오른쪽 상단 배지(예약/완료만) -->
                <div class="topRight">
                    <span class="reward">₩ ${e.reward}</span>

                    <c:choose>
                        <c:when test="${e.status eq '예약'}">
                            <span class="statePill reserved">예약</span>
                        </c:when>
                        <c:when test="${e.status eq '완료'}">
                            <span class="statePill done">완료</span>
                        </c:when>
                        <c:otherwise>
                            <!-- 모집중이면 아무 것도 표시 안 함 -->
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- 제목 -->
                <h3>${e.title}</h3>

                <!-- 시간 -->
                <div class="row" style="margin-top:6px;">
                    <span style="font-size:12px;color:var(--muted);font-weight:900;">${e.time}</span>
                </div>

                <!-- From/To -->
                <div class="meta">
                    <div>From: <b>${e.from}</b></div>
                    <div>To: <b>${e.to}</b></div>
                </div>

                <!-- 해시태그(텍스트 느낌) -->
                <div class="hashtags">
                    <c:if test="${not empty e.hashtags}">
                        <div class="hashtags">
                            <c:forEach var="h" items="${fn:split(e.hashtags, ',')}">
                                <span class="hashText">${fn:trim(h)}</span>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>

                <!-- 하단 작성시간 -->
                <div class="footer">
                    <span>${e.createdAt}</span>
                </div>

            </a>
        </c:forEach>
    </div>

    <!-- Floating Write Button -->
    <a href="<%=request.getContextPath()%>/errand/create" class="fab-write">
        + 글쓰기
    </a>

</div>
</body>
</html>