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
    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">게시글 수정</p>
                <p class="subtitle">내용을 업데이트해요 ✨</p>
            </div>
        </div>
        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">뒤로</a>
        </div>
    </div>

    <form class="formCard" method="post"
          action="<%=request.getContextPath()%>/errand/edit"
          enctype="multipart/form-data">

        <input type="hidden" name="id" value="${e.id}" />

        <label class="label">제목</label>
        <input class="input" name="title" value="${e.title}" required />

        <label class="label">연락처</label>
        <div class="row2">
        <input class="input"
               name="phone"
               value="${e.phone}"
               placeholder="010-1234-5678"
               required />
        </div>

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
            <label class="label">기존 사진</label>

            <div id="existingGrid" class="photo-grid">
                <c:forEach var="img" items="${images}">
                    <div class="thumb-preview existing-thumb" data-image-id="${img.imageId}">
                        <img
                                src="${pageContext.request.contextPath}/assets/upload/${img.imagePath}"
                                onerror="this.src='${pageContext.request.contextPath}/assets/upload/default.png'"
                                alt="첨부">
                        <button type="button" class="thumb-remove existing-remove" aria-label="remove">×</button>
                    </div>
                </c:forEach>
            </div>

            <!-- X 누르면 여기에 hidden이 쌓임 -->
            <div id="deleteHiddenBox"></div>
        </c:if>

        <!-- ✅ 새 사진 추가 -->
        <label class="label">새 사진 추가</label>
        <input id="editImagesInput" class="input" type="file" name="images" accept="image/*" multiple />

        <div id="editPreviewGrid" class="photo-grid"></div>

        <div class="formActions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">취소</a>
            <button class="btn primary" type="submit">저장</button>
        </div>

    </form>

</div>
<script>
    const editInput = document.getElementById('editImagesInput');
    const editPreviewGrid = document.getElementById('editPreviewGrid');

    let editFiles = [];
    let editObjectUrls = [];

    editInput.addEventListener('change', () => {
        cleanupEditUrls();
        editFiles = Array.from(editInput.files);
        renderEditPreviews();
    });

    function renderEditPreviews() {
        editPreviewGrid.innerHTML = "";

        editFiles.forEach((file, idx) => {
            const wrap = document.createElement('div');
            wrap.className = 'thumb-preview';

            const img = document.createElement('img');
            const url = URL.createObjectURL(file);
            editObjectUrls.push(url);
            img.src = url;

            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'thumb-remove';
            btn.textContent = '×';
            btn.addEventListener('click', () => removeEditFile(idx));

            wrap.appendChild(img);
            wrap.appendChild(btn);
            editPreviewGrid.appendChild(wrap);
        });
    }

    function removeEditFile(removeIdx) {
        cleanupEditUrls();

        editFiles = editFiles.filter((_, idx) => idx !== removeIdx);

        const dt = new DataTransfer();
        editFiles.forEach(f => dt.items.add(f));
        editInput.files = dt.files;

        renderEditPreviews();
    }

    function cleanupEditUrls() {
        editObjectUrls.forEach(u => URL.revokeObjectURL(u));
        editObjectUrls = [];
    }
</script>
<script>
    // ===== 기존 사진: X 누르면 바로 화면에서 제거 + deleteImageIds 추가 =====
    const existingGrid = document.getElementById('existingGrid');
    const deleteHiddenBox = document.getElementById('deleteHiddenBox');

    if (existingGrid) {
        existingGrid.addEventListener('click', (e) => {
            const btn = e.target.closest('.existing-remove');
            if (!btn) return;

            const thumb = btn.closest('.existing-thumb');
            if (!thumb) return;

            const imageId = thumb.dataset.imageId;
            if (!imageId) return;

            // hidden input 추가 (중복 방지)
            if (!deleteHiddenBox.querySelector(`input[name="deleteImageIds"][value="${imageId}"]`)) {
                const hidden = document.createElement('input');
                hidden.type = 'hidden';
                hidden.name = 'deleteImageIds';
                hidden.value = imageId;
                deleteHiddenBox.appendChild(hidden);
            }

            // 화면에서 즉시 제거 (새 사진 X랑 동일 UX)
            thumb.remove();
        });
    }
</script>
</body>
</html>