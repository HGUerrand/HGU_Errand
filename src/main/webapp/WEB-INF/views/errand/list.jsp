<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>HGU Errand - List</title>
    <style>
        :root{
            --burgundy:#6B0F1A;
            --green:#14532D;
            --cream:#FFF7ED;
            --ink:#111827;
            --muted:#6B7280;
            --border:#E7E5E4;
            --card:#FFFFFF;
        }
        *{box-sizing:border-box;}
        body{
            margin:0;
            font-family: -apple-system, BlinkMacSystemFont, "Apple SD Gothic Neo", "Noto Sans KR", sans-serif;
            background: radial-gradient(1200px 700px at 18% 10%, rgba(107,15,26,0.12), transparent 60%),
            radial-gradient(1100px 700px at 82% 8%, rgba(20,83,45,0.12), transparent 55%),
            var(--cream);
            color:var(--ink);
        }

        /* ✅ 화면을 더 크게 쓰기 */
        .container{
            max-width: 1280px;
            margin: 0 auto;
            padding: 34px 22px 80px;
        }

        .topbar{
            display:flex;
            align-items:center;
            justify-content:space-between;
            gap:12px;
            margin-bottom: 18px;
        }
        .brand{ display:flex; align-items:center; gap:12px; }
        .logo{
            width:44px; height:44px; border-radius:14px;
            background: linear-gradient(135deg, var(--burgundy), var(--green));
            box-shadow: 0 14px 28px rgba(0,0,0,0.14);
        }
        .title{ margin:0; font-size: 22px; font-weight: 900; letter-spacing: -0.2px; }
        .subtitle{ margin:3px 0 0; font-size: 13px; color: var(--muted); }

        .actions{ display:flex; gap:10px; align-items:center; }
        .btn{
            display:inline-flex;
            align-items:center;
            gap:8px;
            padding: 10px 14px;
            border-radius: 14px;
            border:1px solid var(--border);
            background: rgba(255,255,255,0.80);
            color: var(--ink);
            text-decoration:none;
            font-weight:800;
            font-size: 13px;
            backdrop-filter: blur(10px);
            cursor:pointer;
        }
        .btn.primary{
            border:none;
            background: linear-gradient(135deg, var(--burgundy), var(--green));
            color:white;
            box-shadow: 0 14px 28px rgba(20,83,45,0.18);
        }

        .searchbar{
            display:flex; gap:10px; align-items:center;
            padding: 14px 14px;
            border:1px solid var(--border);
            border-radius: 18px;
            background: rgba(255,255,255,0.80);
            backdrop-filter: blur(10px);
            margin-bottom: 14px;
        }
        .searchbar input{
            width:100%;
            border:none;
            outline:none;
            background:transparent;
            font-size: 15px;
            font-weight: 700;
        }

        .filters{
            display:flex; gap:8px; flex-wrap:wrap;
            margin-bottom: 18px;
        }
        .chip{
            padding:9px 12px;
            border-radius: 999px;
            border:1px solid var(--border);
            background: rgba(255,255,255,0.75);
            font-size:12px;
            font-weight:900;
            color: var(--muted);
        }
        .chip.active{
            color:white;
            border:none;
            background: rgba(107,15,26,0.92);
        }

        /* ✅ 카드가 커지도록 grid/간격/폰트 키움 */
        .grid{
            display:grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
        }
        @media (min-width: 1300px){
            .grid{ grid-template-columns: repeat(4, 1fr); }
        }
        @media (max-width: 980px){
            .grid{ grid-template-columns: repeat(2, 1fr); }
        }
        @media (max-width: 640px){
            .grid{ grid-template-columns: 1fr; }
        }

        .card{
            background: var(--card);
            border:1px solid var(--border);
            border-radius: 20px;
            padding: 16px 16px 14px;
            box-shadow: 0 12px 24px rgba(0,0,0,0.07);
            transition: transform .12s ease, box-shadow .12s ease;
            text-decoration:none;
            color:inherit;
            display:block;
            min-height: 168px;
        }
        .card:hover{
            transform: translateY(-2px);
            box-shadow: 0 20px 34px rgba(0,0,0,0.12);
        }

        .row{
            display:flex; align-items:center; justify-content:space-between; gap:10px;
        }

        .reward{
            font-size: 15px;
            font-weight: 1000;
            color: var(--burgundy);
            letter-spacing: -0.2px;
        }

        /* ✅ 예약/완료 버튼 */
        .stateBtns{
            display:flex;
            gap:8px;
            align-items:center;
        }
        .miniBtn{
            border:1px solid var(--border);
            background: rgba(255,255,255,0.85);
            border-radius: 999px;
            padding: 7px 10px;
            font-size: 11px;
            font-weight: 900;
            color: var(--ink);
            cursor:pointer;
        }
        .miniBtn.reserve{
            border-color: rgba(20,83,45,0.25);
            color: var(--green);
            background: rgba(20,83,45,0.08);
        }
        .miniBtn.done{
            border-color: rgba(107,15,26,0.25);
            color: var(--burgundy);
            background: rgba(107,15,26,0.08);
        }

        .card h3{
            margin: 10px 0 8px;
            font-size: 17px;
            font-weight: 1000;
            line-height: 1.25;
            letter-spacing:-0.3px;
        }

        .badge{
            font-size: 11px;
            font-weight: 1000;
            padding: 6px 9px;
            border-radius: 999px;
            background: rgba(20,83,45,0.10);
            color: var(--green);
            border: 1px solid rgba(20,83,45,0.20);
        }
        .badge.warn{
            background: rgba(107,15,26,0.10);
            color: var(--burgundy);
            border: 1px solid rgba(107,15,26,0.20);
        }

        .meta{
            display:grid;
            grid-template-columns: 1fr 1fr;
            gap: 8px 10px;
            margin-top: 10px;
            color: var(--muted);
            font-size: 12px;
            font-weight: 800;
        }
        .meta b{ color: var(--ink); font-weight: 1000; }

        /* ✅ 해시태그 */
        .hashtags{
            display:flex;
            flex-wrap:wrap;
            gap:8px;
            margin-top: 8px;
        }
        .hash{
            font-size: 11px;
            font-weight: 950;
            color: var(--green);
            background: rgba(20,83,45,0.08);
            border: 1px solid rgba(20,83,45,0.18);
            padding: 6px 10px;
            border-radius: 999px;
        }

        /* ✅ 하단 작성시간 */
        .footer{
            margin-top: 10px;
            display:flex;
            justify-content:space-between;
            align-items:center;
            color: #9CA3AF;
            font-size: 11.5px;
            font-weight: 800;
        }
    </style>
</head>

<body>
<div class="container">

    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">HGU Errand</p>
                <p class="subtitle">크리스마스 톤으로 심부름 빠르게 매칭 🎄</p>
            </div>
        </div>

        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/">홈</a>
            <a class="btn primary" href="<%=request.getContextPath()%>/errand/create">+ 글쓰기</a>
        </div>
    </div>

    <div class="searchbar">
        <span style="font-weight:1000;color:var(--muted);">🔎</span>
        <input placeholder="제목/장소로 검색 (UI만 먼저)" />
        <span class="chip active">전체</span>
    </div>

    <div class="filters">
        <span class="chip active">모집중</span>
        <span class="chip">마감임박</span>
        <span class="chip">심부름</span>
        <span class="chip">대리구매</span>
        <span class="chip">픽업</span>
    </div>

    <div class="grid">
        <div class="card ${e.status eq '완료' ? 'doneCard' : ''}">

            <!-- 상단: 가격 + (예약/완료면 배지 표시, 모집중이면 아무것도 없음) -->
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
                        <!-- 모집중이면 아무것도 안 보여줌 -->
                    </c:otherwise>
                </c:choose>
            </div>

            <h3>${e.title}</h3>

            <!-- 상태 + 시간: 여기 상태 배지는 그대로 쓰고 싶으면 유지, 싫으면 지워도 됨 -->
            <div class="row" style="margin-top:6px;">
                <c:choose>
                    <c:when test="${e.status eq '마감임박'}">
                        <span class="badge warn">마감임박</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge">모집중</span>
                    </c:otherwise>
                </c:choose>
                <span style="font-size:12px;color:var(--muted);font-weight:900;">${e.time}</span>
            </div>

            <!-- From/To -->
            <div class="meta">
                <div>From: <b>${e.from}</b></div>
                <div>To: <b>${e.to}</b></div>
            </div>

            <!-- 해시태그 (텍스트 느낌) -->
            <div class="hashtags">
                <c:forEach var="h" items="${e.hashtags}">
                    <span class="hashText">${h}</span>
                </c:forEach>
            </div>

            <!-- 하단 작성시간 -->
            <div class="footer">
                <span>작성: ${e.createdAt}</span>
            </div>

        </div>
    </div>

</div>

<script>
    // 지금은 UI만. 나중에 예약/완료 API 붙이면 여기 대신 form submit로 바꾸면 됨.
    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("miniBtn")) {
            alert("UI만 연결됨 (다음 단계에서 예약/완료 기능 붙일게)");
        }
    });
</script>
</body>
</html>