<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>심부름 수정</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-form.css">
</head>
<body>
<div class="container narrow">

    <h2 style="margin:0 0 12px;">게시글 수정</h2>

    <form class="formCard" method="post"
          action="<%=request.getContextPath()%>/errand/edit"
          enctype="multipart/form-data">

        <input type="hidden" name="id" value="${e.id}" />

        <label class="label">제목</label>
        <input class="input" name="title" value="${e.title}" required />

        <div class="row2">
            <div>
                <label class="label">보수(원)</label>
                <input class="input" type="number" name="reward" value="${e.reward}" required />
            </div>
            <div>
                <label class="label">시간</label>
                <input class="input" name="time" value="${e.time}" required />
            </div>
        </div>

        <div class="row2">
            <div>
                <label class="label">From</label>
                <input class="input" name="from" value="${e.from}" required />
            </div>
            <div>
                <label class="label">To</label>
                <input class="input" name="to" value="${e.to}" required />
            </div>
        </div>

        <label class="label">상세 설명</label>
        <textarea class="textarea" name="description" rows="5">${e.description}</textarea>

        <label class="label">해시태그</label>
        <input class="input" name="hashtags" value="${e.hashtags}" />

        <!-- ✅ 기존 사진 -->
        <c:if test="${not empty images}">
            <label class="label">기존 사진 (삭제할 것 선택)</label>
            <div class="editPhotosGrid">
                <c:forEach var="img" items="${images}">
                    <label class="editThumb" style="position:relative; display:block;">
                        <img src="<%=request.getContextPath()%>/upload/${img.imagePath}" alt="첨부">

                        <input type="checkbox" name="deleteImageIds" value="${img.imageId}"
                               style="position:absolute; top:8px; left:8px; transform:scale(1.2);">
                    </label>
                </c:forEach>
            </div>
            <div style="margin-top:8px; font-size:12px; color:#6B7280; font-weight:800;">
                체크된 사진은 저장 시 삭제됩니다.
            </div>
        </c:if>

        <!-- ✅ 새 사진 추가 -->
        <label class="label">새 사진 추가</label>
        <input class="input" type="file" name="images" accept="image/*" multiple />

        <div class="formActions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">취소</a>
            <button class="btn primary" type="submit">저장</button>
        </div>

    </form>

</div>
</body>
</html>