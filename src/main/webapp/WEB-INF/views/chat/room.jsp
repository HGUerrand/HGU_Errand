<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>채팅방</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/chat.css">
</head>
<body>
<div class="container narrow chatRoomWrap">

    <div class="chatHeaderText">
        <img class="chatAvatar"
             src="<%=request.getContextPath()%>/upload/${empty header.opponentAvatar ? 'default.png' : header.opponentAvatar}"
             alt="profile">
        <div class="textCol">
            <div class="chatWith"><c:out value="${header.opponentName}"/></div>
            <div class="chatErrand"><c:out value="${header.errandTitle}"/></div>
        </div>
    </div>

    <div id="msgBox" class="msgBox">
        <c:forEach var="m" items="${messages}">
            <div class="msgRow ${m.senderId == myId ? 'me' : 'you'}" data-id="${m.messageId}">
                <div class="bubble">
                    <div class="content"><c:out value="${m.content}"/></div>
                    <div class="meta"><c:out value="${m.createdAt}"/></div>
                </div>
            </div>
        </c:forEach>
    </div>

    <form id="sendForm" class="sendBar" onsubmit="return false;">
        <input type="hidden" id="roomId" value="${header.roomId}"/>
        <input type="text" id="content" class="sendInput" placeholder="메시지 입력..." autocomplete="off"/>
        <button type="button" id="sendBtn" class="sendBtn">전송</button>
    </form>

</div>

<script>
    const ctx = "<%=request.getContextPath()%>";
    const roomId = document.getElementById('roomId').value;
    const box = document.getElementById('msgBox');
    const input = document.getElementById('content');
    const sendBtn = document.getElementById('sendBtn');
    const myId = Number("${myId}");

    let lastId = 0;
    const lastEl = box.querySelector('.msgRow:last-child');
    if (lastEl) lastId = parseInt(lastEl.dataset.id || "0", 10);

    function scrollBottom() {
        box.scrollTop = box.scrollHeight;
    }
    scrollBottom();

    async function poll() {
        try {
            const res = await fetch(`${ctx}/chat/messages?roomId=${roomId}&afterId=${lastId}`);
            const data = await res.json();

            if (data.messages && data.messages.length > 0) {
                data.messages.forEach(m => appendMsg(m));
                lastId = data.lastId || lastId;
                scrollBottom();
            }
        } catch (e) {}
    }

    function escapeHtml(s){
        return (s||"").replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
            .replaceAll("\"","&quot;").replaceAll("'","&#039;");
    }

    function appendMsg(m){
        const row = document.createElement('div');
        row.className = 'msgRow ' + (Number(m.senderId) === myId ? 'me' : 'you');
        row.dataset.id = m.messageId;

        row.innerHTML =
            '<div class="bubble">' +
            '<div class="content">' + escapeHtml(m.content) + '</div>' +
            '<div class="meta">' + escapeHtml(m.createdAt) + '</div>' +
            '</div>';

        box.appendChild(row);
    }


    async function send(){
        const text = input.value.trim();
        if (!text) return;

        // 1) 화면에 먼저 붙이기(임시)
        appendMsg({
            messageId: (lastId + 1),       // 임시로만
            senderId: myId,
            content: text,
            createdAt: new Date().toISOString().slice(0,16).replace('T',' ')
        });

        input.value = "";
        scrollBottom();

        // 2) 서버에 전송
        const form = new URLSearchParams();
        form.append("roomId", roomId);
        form.append("content", text);

        await fetch(ctx + "/chat/send", {
            method: "POST",
            headers: {"Content-Type":"application/x-www-form-urlencoded"},
            body: form.toString()
        });

        // 3) DB 기준으로 다시 당겨서 정리
        await poll();
    }

    sendBtn.addEventListener('click', send);
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') send();
    });

    setInterval(poll, 1500);
</script>
</body>
</html>