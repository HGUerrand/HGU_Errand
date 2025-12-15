package com.hguerrand.errand.dao;

import com.hguerrand.errand.vo.ChatMessageVO;
import com.hguerrand.errand.vo.ChatRoomVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ChatDAO {

    private final JdbcTemplate jdbcTemplate;

    public ChatDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** errandId + requesterId + helperId 기준으로 방 찾기 */
    public Integer findRoomId(int errandId, int requesterId, int helperId) {
        String sql = "SELECT room_id FROM chat_room WHERE errand_id=? AND requester_id=? AND helper_id=?";
        List<Integer> list = jdbcTemplate.query(sql, new Object[]{errandId, requesterId, helperId},
                (rs, rowNum) -> rs.getInt("room_id"));
        return list.isEmpty() ? null : list.get(0);
    }

    /** 없으면 생성하고 roomId 반환 */
    public int createRoom(int errandId, int requesterId, int helperId) {
        String sql = "INSERT INTO chat_room (errand_id, requester_id, helper_id) VALUES (?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, errandId);
            ps.setInt(2, requesterId);
            ps.setInt(3, helperId);
            return ps;
        }, kh);

        return kh.getKey().intValue();
    }

    /** 리스트 탭용: 전체/해주세요(requester)/해줄게요(helper) */
    public List<ChatRoomVO> findRoomsForUser(int myId, String tab) {
        String where;
        Object[] params;

        if ("requester".equals(tab)) {
            where = "cr.requester_id = ?";
            // opponentName(1) + where(1)
            params = new Object[]{myId, myId};

        } else if ("helper".equals(tab)) {
            where = "cr.helper_id = ?";
            // opponentName(1) + where(1)
            params = new Object[]{myId, myId};

        } else { // all
            where = "(cr.requester_id = ? OR cr.helper_id = ?)";
            // opponentName(1) + where(2)
            params = new Object[]{myId, myId, myId};
        }

        String sql =
                "SELECT cr.room_id, cr.errand_id, cr.requester_id, cr.helper_id, " +
                        "       e.title AS errandTitle, " +
                        "       cr.last_message AS lastMessage, " +
                        "       DATE_FORMAT(COALESCE(cr.last_at, cr.created_at), '%Y-%m-%d %H:%i') AS lastAt, " +
                        "       CASE WHEN cr.requester_id = ? THEN mh.name ELSE mr.name END AS opponentName " +
                        "FROM chat_room cr " +
                        "JOIN errand e ON e.id = cr.errand_id " +
                        "JOIN member mr ON mr.member_id = cr.requester_id " +
                        "JOIN member mh ON mh.member_id = cr.helper_id " +
                        "WHERE " + where + " " +
                        "ORDER BY COALESCE(cr.last_at, cr.created_at) DESC";

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            ChatRoomVO r = new ChatRoomVO();
            r.setRoomId(rs.getInt("room_id"));
            r.setErrandId(rs.getInt("errand_id"));
            r.setRequesterId(rs.getInt("requester_id"));
            r.setHelperId(rs.getInt("helper_id"));
            r.setErrandTitle(rs.getString("errandTitle"));
            r.setOpponentName(rs.getString("opponentName"));
            r.setLastMessage(rs.getString("lastMessage"));
            r.setLastAt(rs.getString("lastAt"));
            return r;
        });
    }

    public List<ChatMessageVO> findRecentMessages(int roomId, int limit) {
        String sql =
                "SELECT m.message_id, m.room_id, m.sender_id, mem.name AS senderName, " +
                        "       m.content, DATE_FORMAT(m.created_at, '%H:%i') AS createdAt " +
                        "FROM chat_message m " +
                        "JOIN member mem ON mem.member_id = m.sender_id " +
                        "WHERE m.room_id=? " +
                        "ORDER BY m.message_id DESC " +
                        "LIMIT ?";
        List<ChatMessageVO> list = jdbcTemplate.query(sql, new Object[]{roomId, limit}, (rs, rowNum) -> {
            ChatMessageVO msg = new ChatMessageVO();
            msg.setMessageId(rs.getInt("message_id"));
            msg.setRoomId(rs.getInt("room_id"));
            msg.setSenderId(rs.getInt("sender_id"));
            msg.setSenderName(rs.getString("senderName"));
            msg.setContent(rs.getString("content"));
            msg.setCreatedAt(rs.getString("createdAt"));
            return msg;
        });
        // 최신순으로 뽑았으니 화면에서는 오래된 → 최신 순으로 보여주게 뒤집기
        java.util.Collections.reverse(list);
        return list;
    }

    public List<ChatMessageVO> findMessagesAfter(int roomId, int afterId) {
        String sql =
                "SELECT m.message_id, m.room_id, m.sender_id, mem.name AS senderName, " +
                        "       m.content, DATE_FORMAT(m.created_at, '%H:%i') AS createdAt " +
                        "FROM chat_message m " +
                        "JOIN member mem ON mem.member_id = m.sender_id " +
                        "WHERE m.room_id=? AND m.message_id > ? " +
                        "ORDER BY m.message_id ASC";
        return jdbcTemplate.query(sql, new Object[]{roomId, afterId}, (rs, rowNum) -> {
            ChatMessageVO msg = new ChatMessageVO();
            msg.setMessageId(rs.getInt("message_id"));
            msg.setRoomId(rs.getInt("room_id"));
            msg.setSenderId(rs.getInt("sender_id"));
            msg.setSenderName(rs.getString("senderName"));
            msg.setContent(rs.getString("content"));
            msg.setCreatedAt(rs.getString("createdAt"));
            return msg;
        });
    }

    public int insertMessage(int roomId, int senderId, String content) {
        String sql = "INSERT INTO chat_message (room_id, sender_id, content) VALUES (?, ?, ?)";
        int updated = jdbcTemplate.update(sql, roomId, senderId, content);

        // room에 last 업데이트
        jdbcTemplate.update(
                "UPDATE chat_room SET last_message=?, last_at=NOW() WHERE room_id=?",
                content.length() > 255 ? content.substring(0, 255) : content,
                roomId
        );
        return updated;
    }

    public ChatRoomVO findRoomHeader(int roomId, int myId) {
        String sql =
                "SELECT cr.room_id, cr.errand_id, cr.requester_id, cr.helper_id, " +
                        "       e.title AS errandTitle, " +
                        "       CASE WHEN cr.requester_id = ? THEN mh.name ELSE mr.name END AS opponentName, " +
                        "       CASE WHEN cr.requester_id = ? THEN mh.avatar ELSE mr.avatar END AS opponentAvatar " +
                        "FROM chat_room cr " +
                        "JOIN errand e ON e.id = cr.errand_id " +
                        "JOIN member mr ON mr.member_id = cr.requester_id " +
                        "JOIN member mh ON mh.member_id = cr.helper_id " +
                        "WHERE cr.room_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{myId, myId, roomId}, (rs, rowNum) -> {
            ChatRoomVO r = new ChatRoomVO();
            r.setRoomId(rs.getInt("room_id"));
            r.setErrandId(rs.getInt("errand_id"));
            r.setRequesterId(rs.getInt("requester_id"));
            r.setHelperId(rs.getInt("helper_id"));
            r.setErrandTitle(rs.getString("errandTitle"));
            r.setOpponentName(rs.getString("opponentName"));
            r.setOpponentAvatar(rs.getString("opponentAvatar"));
            return r;
        });
    }
}
