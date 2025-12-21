package com.hguerrand.errand.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ErrandDAO {

    private final JdbcTemplate jdbcTemplate;

    public ErrandDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Map<String, Object>> findAll(String category) {
        return findAll(category, null);
    }

    public List<Map<String, Object>> findAll(String category, String q) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, title, reward, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, description, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if ("low".equals(category)) {
            sql.append("AND reward BETWEEN 0 AND 2000 ");
        } else if ("mid".equals(category)) {
            sql.append("AND reward BETWEEN 2000 AND 5000 ");
        } else if ("high".equals(category)) {
            sql.append("AND reward >= 5000 ");
        }

        // ✅ 검색어
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (");
            sql.append(" title LIKE ? ");
            sql.append(" OR description LIKE ? ");
            sql.append(" OR from_place LIKE ? ");
            sql.append(" OR to_place LIKE ? ");
            sql.append(" OR hashtags LIKE ? ");
            sql.append(") ");

            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append("ORDER BY id DESC");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public int insert(Map<String, Object> e, int requesterId) {
        String sql =
                "INSERT INTO errand " +
                        "(title, reward, from_place, to_place, time_text, status, hashtags, description, image_path, requester_id) " +
                        "VALUES (?, ?, ?, ?, ?, '모집중', ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                e.get("title"),
                e.get("reward"),
                e.get("from"),
                e.get("to"),
                e.get("time"),
                e.get("hashtags"),
                e.get("description"),
                e.get("imagePath"),
                requesterId
        );
    }

    public int updateStatus(int id, String status) {
        return jdbcTemplate.update("UPDATE errand SET status=? WHERE id=?", status, id);
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM errand WHERE id=?", id);
    }

    public List<Map<String, Object>> findByRequesterId(int requesterId) {
        String sql =
                "SELECT id, title, reward, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE requester_id=? ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql, requesterId);
    }

    public Map<String, Object> findById(int id) {
        String sql =
                "SELECT e.id, e.title, e.reward, " +
                        "       e.from_place AS `from`, e.to_place AS `to`, e.time_text AS `time`, " +
                        "       e.status, e.hashtags, e.description, e.image_path AS imagePath, " +
                        "       e.requester_id AS requesterId, " +
                        "       DATE_FORMAT(e.created_at, '%Y-%m-%d %H:%i') AS createdAt, " +
                        "       m.name AS writer_name, m.avatar AS writer_avatar " +   // ✅ 여기!
                        "FROM errand e " +
                        "LEFT JOIN member m ON m.member_id = e.requester_id " +
                        "WHERE e.id=?";

        return jdbcTemplate.queryForMap(sql, id);
    }

    public int updateTextOnly(Map<String, Object> e) {
        String sql =
                "UPDATE errand SET title=?, reward=?, from_place=?, to_place=?, time_text=?, " +
                        "hashtags=?, description=? WHERE id=?";
        return jdbcTemplate.update(sql,
                e.get("title"),
                e.get("reward"),
                e.get("from"),
                e.get("to"),
                e.get("time"),
                e.get("hashtags"),
                e.get("description"),
                e.get("id")
        );
    }

    public List<Map<String, Object>> findImagesByErrandId(int errandId) {
        String sql = "SELECT image_id AS imageId, image_path AS imagePath FROM errand_image WHERE errand_id=? ORDER BY image_id ASC";
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

    public int insertAndReturnId(Map<String, Object> e, int requesterId) {
        String sql =
                "INSERT INTO errand " +
                        "(title, reward, from_place, to_place, time_text, status, hashtags, description, requester_id) " +
                        "VALUES (?, ?, ?, ?, ?, '모집중', ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, (String) e.get("title"));
            ps.setInt(2, (Integer) e.get("reward"));
            ps.setString(3, (String) e.get("from"));
            ps.setString(4, (String) e.get("to"));
            ps.setString(5, (String) e.get("time"));
            ps.setString(6, (String) e.get("hashtags"));
            ps.setString(7, (String) e.get("description"));
            ps.setInt(8, requesterId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int insertAndReturnId(String title, int reward, String from, String to, String time,
                                 String hashtags, String description, int requesterId) {

        String sql = "INSERT INTO errand (title, reward, from_place, to_place, time_text, status, hashtags, description, requester_id) " +
                "VALUES (?, ?, ?, ?, ?, '모집중', ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setInt(2, reward);
            ps.setString(3, from);
            ps.setString(4, to);
            ps.setString(5, time);
            ps.setString(6, hashtags);
            ps.setString(7, description);
            ps.setInt(8, requesterId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }


}