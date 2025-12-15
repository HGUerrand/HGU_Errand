<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>My Page - HGU Errand</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-list.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/mypage.css">
</head>
<body>

<c:if test="${not empty param.toast}">
    <div id="toast" class="toast"></div>
</c:if>

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

    <!-- ✅ 프로필 카드 -->
    <div class="profileCard">

        <div class="profileRow">
            <!-- 왼쪽: 아바타 + 연필 -->
            <div class="avatarWrap">
                <img id="avatarImg" class="avatarImg"
                     src="<%=request.getContextPath()%>/upload/<c:out value='${empty me.avatar ? "default.png" : me.avatar}'/>"
                     alt="avatar" />
                <button type="button" class="iconBtn avatarEdit" id="toggleAvatar" title="사진 수정">✎</button>
            </div>

            <!-- 오른쪽: 닉네임 + 아이디 + 연필 -->
            <div class="profileMain">
                <div class="nameBlock">
                    <div class="nameLine">
                        <div class="nameText"><c:out value="${me.name}"/></div>
                        <button type="button" class="iconBtn" id="toggleName" title="닉네임 수정">✎</button>
                    </div>
                    <div class="idText">@<c:out value="${me.login_id}"/></div>
                </div>
            </div>
        </div>

        <!-- ✅ 닉네임 편집 -->
        <div class="editArea" id="nameEditArea">
            <form method="post" action="<%=request.getContextPath()%>/mypage/name" class="inlineRow" id="nameForm">
                <input class="textInput" name="name" value="<c:out value='${me.name}'/>" placeholder="새 닉네임" />
                <button class="btnSmall btnPrimary" type="submit" id="nameSaveBtn">저장</button>
                <button class="btnSmall" type="button" id="cancelName">취소</button>
            </form>
        </div>

        <!-- ✅ 사진 편집 (선택 즉시 미리보기) -->
        <div class="editArea" id="avatarEditArea">
            <form method="post" action="<%=request.getContextPath()%>/mypage/avatar"
                  enctype="multipart/form-data"
                  class="inlineRow"
                  id="avatarForm">

                <input id="avatarFile" class="fileHidden" type="file" name="avatarFile" accept="image/*" />
                <button type="button" class="btnSmall" id="chooseAvatar">사진 선택</button>
                <div class="fileName" id="pickedName">선택된 파일 없음</div>

                <button class="btnSmall btnPrimary" type="submit" id="avatarUploadBtn">업로드</button>
                <button class="btnSmall" type="button" id="cancelAvatar">취소</button>
            </form>
        </div>

        <div class="hint">
            ✎ 누르면 그 자리에서 수정할 수 있어요.
        </div>

    </div>

    <h3 style="margin:10px 0 12px; font-size:16px;">내가 올린 게시글</h3>

    <div class="errand-grid">
        <c:forEach var="e" items="${myErrands}">
            <a class="card ${e.status eq '완료' ? 'doneCard' : ''}"
               href="<%=request.getContextPath()%>/errand/detail?id=${e.id}">
                <div class="topRight">
                    <span class="reward">₩ <c:out value="${e.reward}"/></span>
                    <c:choose>
                        <c:when test="${e.status eq '예약'}"><span class="statePill reserved">예약</span></c:when>
                        <c:when test="${e.status eq '완료'}"><span class="statePill done">완료</span></c:when>
                    </c:choose>
                </div>
                <h3><c:out value="${e.title}"/></h3>
                <div class="row" style="margin-top:6px;">
                    <span style="font-size:12px;color:var(--muted);font-weight:900;"><c:out value="${e.time}"/></span>
                </div>
                <div class="meta">
                    <div>From: <b><c:out value="${e.from}"/></b></div>
                    <div>To: <b><c:out value="${e.to}"/></b></div>
                </div>
                <div class="footer"><span><c:out value="${e.createdAt}"/></span></div>
            </a>
        </c:forEach>
    </div>

</div>

<script>
    // ===== edit toggle =====
    const nameEditArea = document.getElementById('nameEditArea');
    const avatarEditArea = document.getElementById('avatarEditArea');

    const toggleName = document.getElementById('toggleName');
    const toggleAvatar = document.getElementById('toggleAvatar');
    const cancelName = document.getElementById('cancelName');
    const cancelAvatar = document.getElementById('cancelAvatar');

    function hideAllEdits(){
        nameEditArea.classList.remove('show');
        avatarEditArea.classList.remove('show');
    }

    toggleName.addEventListener('click', () => {
        const willShow = !nameEditArea.classList.contains('show');
        hideAllEdits();
        if (willShow) nameEditArea.classList.add('show');
    });

    toggleAvatar.addEventListener('click', () => {
        const willShow = !avatarEditArea.classList.contains('show');
        hideAllEdits();
        if (willShow) avatarEditArea.classList.add('show');
    });

    cancelName.addEventListener('click', hideAllEdits);

    // ===== avatar choose + preview =====
    const avatarImg = document.getElementById('avatarImg');
    const avatarFile = document.getElementById('avatarFile');
    const chooseAvatar = document.getElementById('chooseAvatar');
    const pickedName = document.getElementById('pickedName');

    let previewUrl = null;
    const originalSrc = avatarImg.getAttribute('src');

    chooseAvatar.addEventListener('click', () => avatarFile.click());

    avatarFile.addEventListener('change', () => {
        if (!avatarFile.files || avatarFile.files.length === 0) {
            pickedName.textContent = "선택된 파일 없음";
            if (previewUrl) URL.revokeObjectURL(previewUrl);
            previewUrl = null;
            avatarImg.src = originalSrc;
            return;
        }

        const f = avatarFile.files[0];
        pickedName.textContent = f.name;

        if (previewUrl) URL.revokeObjectURL(previewUrl);
        previewUrl = URL.createObjectURL(f);
        avatarImg.src = previewUrl; // ✅ 업로드 전 미리보기
    });

    cancelAvatar.addEventListener('click', () => {
        avatarFile.value = "";
        pickedName.textContent = "선택된 파일 없음";
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        previewUrl = null;
        avatarImg.src = originalSrc;
        hideAllEdits();
    });

    // ===== disable buttons while submitting =====
    const nameForm = document.getElementById('nameForm');
    const nameSaveBtn = document.getElementById('nameSaveBtn');
    nameForm.addEventListener('submit', () => {
        nameSaveBtn.disabled = true;
        nameSaveBtn.textContent = "저장중...";
    });

    const avatarForm = document.getElementById('avatarForm');
    const avatarUploadBtn = document.getElementById('avatarUploadBtn');
    avatarForm.addEventListener('submit', () => {
        avatarUploadBtn.disabled = true;
        avatarUploadBtn.textContent = "업로드중...";
    });

    // ===== toast (after redirect) + auto close =====
    const toast = document.getElementById('toast');
    const toastType = "${param.toast}";

    if (toast && toastType) {
        let msg = "저장됐어요 ✅";
        if (toastType === "name") msg = "닉네임 저장됐어요 ✅";
        if (toastType === "avatar") msg = "사진 업로드 됐어요 ✅";

        toast.textContent = msg;
        toast.classList.add('show');
        hideAllEdits(); // ✅ 자동 닫기

        setTimeout(() => toast.classList.remove('show'), 1800);

        // URL에 toast 파라미터 남는 거 싫으면 제거
        try {
            const url = new URL(window.location.href);
            url.searchParams.delete("toast");
            window.history.replaceState({}, "", url.toString());
        } catch(e){}
    }
</script>
</body>
</html>