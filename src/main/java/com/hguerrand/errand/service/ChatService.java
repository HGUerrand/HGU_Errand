package com.hguerrand.errand.service;

import com.hguerrand.errand.dao.ChatDAO;
import com.hguerrand.errand.vo.ChatMessageVO;
import com.hguerrand.errand.vo.ChatRoomVO;
import com.hguerrand.errand.dao.ErrandDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatDAO chatDAO;
    private final ErrandDAO errandDAO;

    public ChatService(ChatDAO chatDAO, ErrandDAO errandDAO) {
        this.chatDAO = chatDAO;
        this.errandDAO = errandDAO;
    }

    /** detail에서 채팅하기 눌렀을 때: 방 찾거나 생성 */
    public int startRoom(int errandId, int myId) {
        Map<String, Object> e = errandDAO.findById(errandId);

        Number n = (Number) e.get("requesterId");  // Integer든 Long이든 다 받음
        int requesterId = n.intValue();

        if (requesterId == myId) {
            throw new IllegalArgumentException("내 게시글에서는 채팅방을 시작할 수 없습니다.");
        }

        Integer roomId = chatDAO.findRoomId(errandId, requesterId, myId);
        if (roomId != null) return roomId;

        return chatDAO.createRoom(errandId, requesterId, myId);
    }

    public List<ChatRoomVO> listRooms(int myId, String tab) {
        return chatDAO.findRoomsForUser(myId, tab);
    }

    public ChatRoomVO roomHeader(int roomId, int myId) {
        return chatDAO.findRoomHeader(roomId, myId);
    }

    public List<ChatMessageVO> recent(int roomId) {
        return chatDAO.findRecentMessages(roomId, 50);
    }

    public List<ChatMessageVO> after(int roomId, int afterId) {
        return chatDAO.findMessagesAfter(roomId, afterId);
    }

    public void send(int roomId, int senderId, String content) {
        if (content == null) return;
        content = content.trim();
        if (content.isEmpty()) return;
        chatDAO.insertMessage(roomId, senderId, content);
    }
}
