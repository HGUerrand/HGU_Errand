<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>HGU Errand - Create</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/errand-form.css">
</head>

<body>
<div class="container">

    <div class="topbar">
        <div class="brand">
            <div class="logo"></div>
            <div>
                <p class="title">새 심부름 등록</p>
                <p class="subtitle">필수만 먼저 입력해도 OK 🎄</p>
            </div>
        </div>

        <div class="actions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/list">목록</a>
        </div>
    </div>

    <form class="formCard" method="post" action="<%=request.getContextPath()%>/errand/create">
        <label class="label">제목</label>
        <input class="input" name="title" placeholder="예) 프린트물 대신 뽑아주기" required />

        <div class="row2">
            <div>
                <label class="label">보수(원)</label>
                <input class="input" type="number" name="reward" min="0" step="100" placeholder="1500" required />
            </div>
            <div>
                <label class="label">시간(표시용)</label>
                <input class="input" name="time" placeholder="예) 오늘 19:00 / 내일 10:30" required />
            </div>
        </div>

        <div class="row2">
            <div>
                <label class="label">From</label>
                <input class="input" name="from" placeholder="학생회관" required />
            </div>
            <div>
                <label class="label">To</label>
                <input class="input" name="to" placeholder="오석관" required />
            </div>
        </div>

        <label class="label">해시태그 (콤마로 구분)</label>
        <input class="input" name="hashtags" placeholder="#급함,#가벼움" />

        <div class="formActions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/list">취소</a>
            <button class="btn primary" type="submit">등록</button>
        </div>
    </form>

</div>
</body>
</html>