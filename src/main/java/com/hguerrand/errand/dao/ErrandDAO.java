package com.hguerrand.errand.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ErrandDAO {

    private final JdbcTemplate jdbcTemplate;

    public ErrandDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        String sql =
                "SELECT id, title, reward, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, description, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
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
                "SELECT id, title, reward, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, description, image_path AS imagePath, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE id=?";

        return jdbcTemplate.queryForMap(sql, id);
    }
}