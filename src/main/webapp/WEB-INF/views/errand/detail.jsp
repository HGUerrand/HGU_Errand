<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <!DOCTYPE html>
            <html lang="ko">

            <head>
                <meta charset="UTF-8" />
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
                                        <%-- ✅ 내 글일 때만 상태 변경 가능 --%>
                                            <%-- ✅ 내 글 또는 관리자일 때 상태 변경 가능 --%>
                                                <c:when test="${e.isMine or loginMember.role eq 'ADMIN'}">
                                                    <c:choose>
                                                        <c:when test="${e.status eq '모집중'}">
                                                            <form method="post"
                                                                action="<%=request.getContextPath()%>/errand/status"
                                                                style="margin:0;">
                                                                <input type="hidden" name="id" value="${e.id}">
                                                                <input type="hidden" name="status" value="예약">
                                                                <button class="cta cta-green" type="submit">예약으로
                                                                    변경</button>
                                                            </form>
                                                        </c:when>

                                                        <c:when test="${e.status eq '예약'}">
                                                            <form method="post"
                                                                action="<%=request.getContextPath()%>/errand/status"
                                                                style="margin:0;">
                                                                <input type="hidden" name="id" value="${e.id}">
                                                                <input type="hidden" name="status" value="완료">
                                                                <button class="cta cta-green" type="submit">완료로
                                                                    변경</button>
                                                            </form>
                                                        </c:when>

                                                        <c:when test="${e.status eq '완료'}">
                                                            <form method="post"
                                                                action="<%=request.getContextPath()%>/errand/status"
                                                                style="margin:0;">
                                                                <input type="hidden" name="id" value="${e.id}">
                                                                <input type="hidden" name="status" value="모집중">
                                                                <button class="cta cta-danger" type="submit">완료 취소
                                                                    (모집중)</button>
                                                            </form>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <span class="statusText">상태:
                                                                <c:out value="${e.status}" />
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>

                                                <%-- ✅ 남의 글: 상태만 표시 --%>
                                                    <c:otherwise>
                                                        <span class="statusText">상태:
                                                            <c:out value="${e.status}" />
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
                                ₩
                                <c:out value="${e.reward}" />
                            </div>
                        </div>

                        <!-- 제목 -->
                        <h1 class="errand-title">
                            <c:out value="${e.title}" />
                        </h1>

                        <!-- 작성자 정보 -->
                        <div class="writer-info">
                            <img class="avatar"
                                src="<%=request.getContextPath()%>/assets/upload/${empty e.writer_avatar ? 'avatar/default.png' : e.writer_avatar}"
                                onerror="this.src='<%=request.getContextPath()%>/assets/upload/default.png'" alt="프로필">
                            <div class="writer-meta">
                                <span class="writer-name">
                                    <c:out value="${empty e.writer_name ? '익명' : e.writer_name}" />
                                </span>
                                <span class="write-date">
                                    <c:out value="${e.createdAt}" /> 작성
                                </span>
                            </div>
                        </div>

                        <!-- 핵심 정보 그리드 (회색 박스 스타일) -->
                        <div class="info-grid">
                            <div class="info-box">
                                <span class="label">From</span>
                                <span class="value">
                                    <c:out value="${e.from}" />
                                </span>
                            </div>
                            <div class="info-box">
                                <span class="label">To</span>
                                <span class="value">
                                    <c:out value="${e.to}" />
                                </span>
                            </div>
                            <div class="info-box full-width">
                                <span class="label">Time</span>
                                <span class="value">
                                    <c:out value="${e.time}" />
                                </span>
                            </div>
                        </div>

                        <!-- 상세 설명 -->
                        <div class="description-section">
                            <span class="section-label">상세 설명</span>
                            <div class="description-text">
                                <c:out value="${e.description}" />
                            </div>

                            <!-- 이미지 (있을 경우에만) -->
                            <c:if test="${not empty images}">
                                <div class="photo-grid">
                                    <c:forEach var="img" items="${images}">
                                        <button type="button" class="thumb"
                                            data-full="<%=request.getContextPath()%>/assets/upload/${img.imagePath}">
                                            <img src="${pageContext.request.contextPath}/assets/upload/${img.imagePath}"
                                                onerror="this.src='${pageContext.request.contextPath}/assets/upload/default.png'">
                                        </button>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <!-- 라이트박스 -->
                            <div id="lightbox" class="lightbox" style="display:none;">
                                <div class="lightbox-bg"></div>
                                <img id="lightboxImg" class="lightbox-img" alt="full">
                            </div>

                            <script>
                                const lb = document.getElementById('lightbox');
                                const lbImg = document.getElementById('lightboxImg');

                                document.addEventListener('click', (e) => {
                                    const btn = e.target.closest('.thumb');
                                    if (btn) {
                                        lbImg.src = btn.dataset.full;
                                        lb.style.display = 'block';
                                        return;
                                    }
                                    if (e.target.classList.contains('lightbox-bg') || e.target.id === 'lightbox') {
                                        lb.style.display = 'none';
                                        lbImg.src = '';
                                    }
                                });

                                document.addEventListener('keydown', (e) => {
                                    if (e.key === 'Escape') {
                                        lb.style.display = 'none';
                                        lbImg.src = '';
                                    }
                                });
                            </script>
                        </div>

                        <!-- 해시태그 -->
                        <c:if test="${not empty e.hashtags}">
                            <div class="hashtag-list">
                                <c:forEach var="h" items="${fn:split(e.hashtags, ',')}">
                                    <span class="hash-item">
                                        <c:out value="${fn:trim(h)}" />
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>

                    </main>
                </div>

                <!-- 하단 고정 액션바 -->
                <div class="bottom-action-bar">
                    <div class="action-inner">
                        <c:choose>
                            <c:when test="${e.isMine or loginMember.role eq 'ADMIN'}">
                                <a href="<%=request.getContextPath()%>/errand/edit?id=${e.id}" class="btn btn-secondary"
                                    style="flex:1; text-align:center;">수정하기</a>

                                <form method="post" action="<%=request.getContextPath()%>/errand/delete"
                                    style="flex:1; margin:0;">
                                    <input type="hidden" name="id" value="${e.id}">
                                    <button type="submit" class="btn btn-danger" style="width:100%;">삭제하기</button>
                                </form>
                            </c:when>

                            <c:otherwise>
                                <div class="btn btn-primary"
                                     style="flex:1; text-align:center; cursor:pointer;"
                                     onclick="copyPhone('${e.phone}')">
                                        ${e.phone}
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <script>
                    function copyPhone(phone) {
                        if (!phone) return;

                        const clean = phone.replace(/-/g, '');

                        navigator.clipboard.writeText(clean).then(() => {
                            alert("전화번호가 복사되었습니다 📋");
                        }).catch(() => {
                            const temp = document.createElement("textarea");
                            temp.value = clean;
                            document.body.appendChild(temp);
                            temp.select();
                            document.execCommand("copy");
                            document.body.removeChild(temp);
                            alert("전화번호가 복사되었습니다 📋");
                        });
                    }
                </script>
            </body>
            </html>