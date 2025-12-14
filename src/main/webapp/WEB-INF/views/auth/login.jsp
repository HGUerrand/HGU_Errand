<%--
  Created by IntelliJ IDEA.
  User: panri
  Date: 2025-12-14
  Time: 오후 11:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>LOGIN</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>

<body>

<div class="container">

    <header class="topbar">
        <div class="brand">HGU Errand</div>
    </header>

    <section class="formCard login-card">
        <h2 class="login-title">로그인</h2>

        <!-- error -->
        <c:if test="${not empty error}">
            <p class="login-error">${error}</p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/auth/login">

            <div class="field">
                <label class="label">ID</label>
                <input class="input" type="text" name="loginId" required>
            </div>

            <div class="field">
                <label class="label">PW</label>
                <input class="input" type="password" name="password" required>
            </div>

            <div class="formActions">
                <button type="submit" class="btn primary login-btn">
                    LOGIN
                </button>
            </div>
        </form>
    </section>

</div>

</body>
</html>

