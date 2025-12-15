<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>채팅</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/chat.css">
</head>
<body>
<div class="container narrow">

    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">채팅</p>
                <p class="subtitle">대화 목록</p>
            </div>
        </div>
        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/list">목록</a>
        </div>
    </div>

    <!-- 탭 -->
    <div class="chatTabs">
        <a class="tab ${tab eq 'all' ? 'active' : ''}"
           href="<%=request.getContextPath()%>/chat/list?tab=all">전체</a>
        <a class="tab ${tab eq 'requester' ? 'active' : ''}"
           href="<%=request.getContextPath()%>/chat/list?tab=requester">해주세요</a>
        <a class="tab ${tab eq 'helper' ? 'active' : ''}"
           href="<%=request.getContextPath()%>/chat/list?tab=helper">해줄게요</a>
    </div>

    <c:if test="${empty rooms}">
        <div class="emptyBox">아직 채팅이 없어요 🥹</div>
    </c:if>

    <div class="roomList">
        <c:forEach var="r" items="${rooms}">
            <a class="roomItem" href="<%=request.getContextPath()%>/chat/room?roomId=${r.roomId}">
                <div class="roomTop">
                    <div class="opponent">${r.opponentName}</div>
                    <div class="time">${r.lastAt}</div>
                </div>
                <div class="title">${r.errandTitle}</div>
                <div class="preview">
                    <c:out value="${empty r.lastMessage ? '대화를 시작해보세요!' : r.lastMessage}"/>
                </div>
            </a>
        </c:forEach>
    </div>

</div>
</body>
</html>