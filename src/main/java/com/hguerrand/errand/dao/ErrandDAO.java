package com.hguerrand.errand.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ErrandDAO {

    private final JdbcTemplate jdbcTemplate;

    public ErrandDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ===================== 리스트 ===================== */
    public List<Map<String, Object>> findAll(String category) {
        return findAll(category, null);
    }

    public List<Map<String, Object>> findAll(String category, String q) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, title, reward, phone, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, description, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // 💰 금액 필터
        if ("low".equals(category)) {
            sql.append("AND reward < 2000 ");
        } else if ("mid".equals(category)) {
            sql.append("AND reward BETWEEN 2000 AND 4999 ");
        } else if ("high".equals(category)) {
            sql.append("AND reward >= 5000 ");
        }

        // 🔍 검색
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR description LIKE ? " +
                    "OR from_place LIKE ? OR to_place LIKE ? OR hashtags LIKE ?) ");
            String like = "%" + q.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(like);
        }

        sql.append("ORDER BY id DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /* ===================== 단건 ===================== */
    public Map<String, Object> findById(int id) {
        String sql =
                "SELECT e.id, e.title, e.reward, e.phone, " +
                        "e.from_place AS `from`, e.to_place AS `to`, e.time_text AS `time`, " +
                        "e.status, e.hashtags, e.description, " +
                        "e.requester_id AS requesterId, " +
                        "DATE_FORMAT(e.created_at, '%Y-%m-%d %H:%i') AS createdAt, " +
                        "m.name AS writer_name, m.avatar AS writer_avatar " +
                        "FROM errand e " +
                        "LEFT JOIN member m ON m.member_id = e.requester_id " +
                        "WHERE e.id=?";

        return jdbcTemplate.queryForMap(sql, id);
    }

    /* ===================== 생성 ===================== */
    public int insertAndReturnId(String title, int reward, String from, String to,
                                 String time, String phone,
                                 String hashtags, String description,
                                 int requesterId) {

        String sql =
                "INSERT INTO errand " +
                        "(title, reward, from_place, to_place, time_text, phone, status, hashtags, description, requester_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, '모집중', ?, ?, ?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setInt(2, reward);
            ps.setString(3, from);
            ps.setString(4, to);
            ps.setString(5, time);
            ps.setString(6, phone);        // ✅ phone
            ps.setString(7, hashtags);
            ps.setString(8, description);
            ps.setInt(9, requesterId);
            return ps;
        }, kh);

        return kh.getKey().intValue();
    }

    /* ===================== 수정 ===================== */
    public int updateTextOnly(Map<String, Object> e) {
        String sql =
                "UPDATE errand SET title=?, reward=?, from_place=?, to_place=?, time_text=?, " +
                        "phone=?, hashtags=?, description=? WHERE id=?";

        return jdbcTemplate.update(sql,
                e.get("title"),
                e.get("reward"),
                e.get("from"),
                e.get("to"),
                e.get("time"),
                e.get("phone"),
                e.get("hashtags"),
                e.get("description"),
                e.get("id")
        );
    }

    public int updateStatus(int id, String status) {
        return jdbcTemplate.update("UPDATE errand SET status=? WHERE id=?", status, id);
    }

    /* ===================== 삭제 ===================== */
    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM errand WHERE id=?", id);
    }

    /* ===================== 이미지 ===================== */
    public List<Map<String, Object>> findImagesByErrandId(int errandId) {
        String sql =
                "SELECT image_id AS imageId, image_path AS imagePath " +
                        "FROM errand_image WHERE errand_id=? ORDER BY image_id ASC";
        return jdbcTemplate.queryForList(sql, errandId);
    }

    public void insertImages(int errandId, List<String> fileNames) {
        String sql = "INSERT INTO errand_image (errand_id, image_path) VALUES (?, ?)";
        for (String fn : fileNames) {
            jdbcTemplate.update(sql, errandId, fn);
        }
    }

    public int deleteImageById(int imageId) {
        return jdbcTemplate.update("DELETE FROM errand_image WHERE image_id=?", imageId);
    }

    /* ===================== 마이페이지 ===================== */
    public List<Map<String, Object>> findByRequesterId(int requesterId) {
        String sql =
                "SELECT id, title, reward, phone, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE requester_id=? ORDER BY id DESC";

        return jdbcTemplate.queryForList(sql, requesterId);
    }
}