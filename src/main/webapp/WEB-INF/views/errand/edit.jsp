<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>HGU Errand - Edit</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-form.css">
</head>
<body>
<div class="container narrow">

    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">게시글 수정</p>
                <p class="subtitle">필요한 것만 바꿔도 돼 🎄</p>
            </div>
        </div>
        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">뒤로</a>
        </div>
    </div>

    <form class="formCard" method="post"
          action="<%=request.getContextPath()%>/errand/edit"
          enctype="multipart/form-data">

        <input type="hidden" name="id" value="${e.id}">
        <input type="hidden" name="oldImage" value="${e.image_path}">

        <label class="label">작성자 이름</label>
        <input class="input" name="writerName" value="${e.writer_name}" />

        <label class="label">제목</label>
        <input class="input" name="title" value="${e.title}" required />

        <div class="row2">
            <div>
                <label class="label">보수(원)</label>
                <input class="input" type="number" name="reward" min="0" step="100" value="${e.reward}" required />
            </div>
            <div>
                <label class="label">시간(표시용)</label>
                <input class="input" name="time" value="${e.time_text}" required />
            </div>
        </div>

        <div class="row2">
            <div>
                <label class="label">From</label>
                <input class="input" name="from" value="${e.from_place}" required />
            </div>
            <div>
                <label class="label">To</label>
                <input class="input" name="to" value="${e.to_place}" required />
            </div>
        </div>

        <label class="label">상세 설명</label>
        <textarea class="textarea" name="description" rows="5">${e.description}</textarea>

        <label class="label">해시태그 (콤마로 구분)</label>
        <input class="input" name="hashtags" value="${e.hashtags}" />

        <label class="label">사진 업로드 (선택)</label>
        <input class="input" type="file" name="image" accept="image/*" />

        <c:if test="${not empty e.image_path}">
            <div style="margin-top:10px; color: var(--muted); font-size: 12px; font-weight: 800;">
                현재 이미지:
            </div>
            <img src="<%=request.getContextPath()%>/upload/${e.image_path}" style="width:100%; border-radius:16px; margin-top:8px;">
        </c:if>

        <div class="formActions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">취소</a>
            <button class="btn primary" type="submit">수정 저장</button>
        </div>
    </form>

</div>
</body>
</html>