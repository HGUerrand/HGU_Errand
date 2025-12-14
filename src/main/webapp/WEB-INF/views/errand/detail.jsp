<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>심부름 상세 - HGU Errand</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-detail.css">
</head>
<body>

<div class="page-container">

    <!-- 상단 네비게이션 -->
    <header class="nav-header">
        <a href="<%=request.getContextPath()%>/errand/list" class="back-btn" aria-label="뒤로가기">
            ←
        </a>
        <div class="page-title">심부름 상세정보</div>
    </header>

    <!-- 메인 카드 -->
    <main class="detail-card">

        <!-- 카드 헤더: 상태/카테고리 + 가격 -->
        <div class="card-header">
            <div class="chip-group">
                <!-- 상태 칩 -->
                <div class="ctaRow">
                    <c:choose>
                        <%-- 내 글일 때만 상태 변경 가능 --%>
                        <c:when test="${e.isMine}">
                            <c:choose>
                                <c:when test="${e.status eq '모집중'}">
                                    <form method="post" action="<%=request.getContextPath()%>/errand/status"
                                          style="margin:0;">
                                        <input type="hidden" name="id" value="${e.id}">
                                        <input type="hidden" name="status" value="예약">
                                        <button class="cta green" type="submit">예약으로 변경</button>
                                    </form>
                                </c:when>

                                <c:when test="${e.status eq '예약'}">
                                    <form method="post" action="<%=request.getContextPath()%>/errand/status"
                                          style="margin:0;">
                                        <input type="hidden" name="id" value="${e.id}">
                                        <input type="hidden" name="status" value="완료">
                                        <button class="cta green" type="submit">완료로 변경</button>
                                    </form>
                                </c:when>

                                <c:when test="${e.status eq '완료'}">
                                    <form method="post" action="<%=request.getContextPath()%>/errand/status"
                                          style="margin:0;">
                                        <input type="hidden" name="id" value="${e.id}">
                                        <input type="hidden" name="status" value="모집중">
                                        <button class="cta danger" type="submit">완료 취소 (모집중)</button>
                                    </form>
                                </c:when>

                                <c:otherwise>
                    <span style="font-size:12px; font-weight:900; color:#6B7280;">
                        상태: <c:out value="${e.status}"/>
                    </span>
                                </c:otherwise>
                            </c:choose>
                        </c:when>

                        <%-- 남의 글이면 상태만 표시 --%>
                        <c:otherwise>
            <span style="font-size:12px; font-weight:900; color:#6B7280;">
                상태: <c:out value="${e.status}"/>
            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- 내 글 표시 -->
                <c:if test="${e.isMine}">
                    <span class="chip category">내 글</span>
                </c:if>
            </div>

            <div class="reward-price">
                ₩ <c:out value="${e.reward}"/>
            </div>
        </div>

        <!-- 제목 -->
        <h1 class="errand-title"><c:out value="${e.title}"/></h1>

        <!-- 작성자 정보 -->
        <div class="writer-info">
            <img class="avatar"
                 src="<%=request.getContextPath()%>/upload/${empty e.writer_avatar ? 'default.png' : e.writer_avatar}"
                 alt="프로필">
            <div class="writer-meta">
                <span class="writer-name"><c:out value="${empty e.writer_name ? '익명' : e.writer_name}"/></span>
                <span class="write-date"><c:out value="${e.createdAt}"/> 작성</span>
            </div>
        </div>

        <!-- 핵심 정보 그리드 (회색 박스 스타일) -->
        <div class="info-grid">
            <div class="info-box">
                <span class="label">From</span>
                <span class="value"><c:out value="${e.from}"/></span>
            </div>
            <div class="info-box">
                <span class="label">To</span>
                <span class="value"><c:out value="${e.to}"/></span>
            </div>
            <div class="info-box full-width">
                <span class="label">Time</span>
                <span class="value"><c:out value="${e.time}"/></span>
            </div>
        </div>

        <!-- 상세 설명 -->
        <div class="description-section">
            <span class="section-label">상세 설명</span>
            <div class="description-text"><c:out value="${e.description}"/></div>

            <!-- 이미지 (있을 경우에만) -->
            <c:if test="${not empty e.image_path}">
                <img class="errand-image" src="<%=request.getContextPath()%>/upload/${e.image_path}" alt="첨부 이미지">
            </c:if>
        </div>

        <!-- 해시태그 -->
        <c:if test="${not empty e.hashtags}">
            <div class="hashtag-list">
                <c:forEach var="h" items="${fn:split(e.hashtags, ',')}">
                    <span class="hash-item"><c:out value="${fn:trim(h)}"/></span>
                </c:forEach>
            </div>
        </c:if>

    </main>
</div>

<!-- 하단 고정 액션바 -->
<div class="bottom-action-bar">
    <div class="action-inner">

        <c:choose>
            <c:when test="${e.isMine}">
                <a href="<%=request.getContextPath()%>/errand/edit?id=${e.id}"
                   class="btn btn-secondary" style="flex:1; text-align:center;">
                    수정하기
                </a>

                <form method="post" action="<%=request.getContextPath()%>/errand/delete"
                      style="flex:1; margin:0;"
                      onsubmit="return confirm('정말 삭제할까? 삭제하면 되돌릴 수 없어!');">
                    <input type="hidden" name="id" value="${e.id}">
                    <button type="submit" class="btn btn-danger" style="width:100%;">
                        삭제하기
                    </button>
                </form>
            </c:when>

            <c:otherwise>
                <a href="<%=request.getContextPath()%>/chat/start?id=${e.id}"
                   class="btn btn-primary" style="flex:1; text-align:center;">
                    채팅하기
                </a>
            </c:otherwise>
        </c:choose>

    </div>
</div>

</body>
</html>