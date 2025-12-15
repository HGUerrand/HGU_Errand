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
<div class="container narrow">

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

    <!-- ✅ multipart -->
    <form class="formCard" method="post"
          action="<%=request.getContextPath()%>/errand/create"
          enctype="multipart/form-data">

        <label class="label">작성자 이름</label>
        <input class="input" name="writerName" placeholder="예) 김한동" />

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

        <label class="label">상세 설명</label>
        <textarea class="textarea" name="description" rows="5"
                  placeholder="예) 올네이션스홀 근처 편의점에서 아이스 아메리카노 1잔 사다주세요!"></textarea>

        <label class="label">해시태그 (콤마로 구분)</label>
        <input class="input" name="hashtags" placeholder="#급함,#가벼움" />

        <label class="label">사진 업로드 (선택)</label>
        <input id="imagesInput" class="input" type="file" name="images" accept="image/*" multiple />

        <ul id="fileList" class="fileList"></ul>

        <script>
            const input = document.getElementById('imagesInput');
            const list = document.getElementById('fileList');

            function renderFiles() {
                list.innerHTML = "";
                const files = Array.from(input.files);
                files.forEach((f, idx) => {
                    const li = document.createElement('li');
                    li.className = "fileItem";
                    li.innerHTML = `
        <span class="fileName">${f.name}</span>
        <button type="button" class="fileRemove" aria-label="remove">×</button>
      `;
                    li.querySelector('.fileRemove').addEventListener('click', () => {
                        const dt = new DataTransfer();
                        files.forEach((file, i) => { if (i !== idx) dt.items.add(file); });
                        input.files = dt.files;
                        renderFiles();
                    });
                    list.appendChild(li);
                });
            }

            input.addEventListener('change', renderFiles);
        </script>

        <div class="formActions">
            <a class="btn" href="<%=request.getContextPath()%>/errand/list">취소</a>
            <button class="btn primary" type="submit">등록</button>
        </div>
    </form>

</div>
</body>
</html>