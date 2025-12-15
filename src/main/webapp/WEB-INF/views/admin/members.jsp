<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 승인</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

<div class="container">
    <h2>회원 승인 대기 목록</h2>

    <c:if test="${empty members}">
        <p class="empty">승인 대기 중인 회원이 없습니다.</p>
    </c:if>

    <c:forEach var="m" items="${members}">
        <div class="member-card">
            <div class="info">
                <p><strong>ID:</strong> ${m.login_id}</p>
                <p><strong>이름:</strong> ${m.name}</p>
                <p><strong>상태:</strong> ${m.status}</p>
            </div>

            <div class="actions">
                <a class="btn-outline"
                   href="${pageContext.request.contextPath}/upload/${m.student_card_path}"
                   target="_blank">
                    학생증 보기
                </a>

                <form method="post"
                      action="${pageContext.request.contextPath}/admin/approve">
                    <input type="hidden" name="memberId" value="${m.member_id}">
                    <button type="submit" class="btn-primary">
                        승인
                    </button>
                </form>
            </div>
        </div>
    </c:forEach>

    <a class="back" href="${pageContext.request.contextPath}/">
        ← 홈으로
    </a>
</div>

</body>
</html>
