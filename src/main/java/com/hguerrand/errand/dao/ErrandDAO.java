package com.hguerrand.errand.dao;

import com.hguerrand.errand.vo.ErrandVO;
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
                        "status, hashtags, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public int insert(Map<String, Object> e) {
        String sql = "INSERT INTO errand (title, reward, from_place, to_place, time_text, status, hashtags, requester_id) " +
                "VALUES (?, ?, ?, ?, ?, '모집중', ?, ?)";
        return jdbcTemplate.update(sql,
                e.get("title"),
                e.get("reward"),
                e.get("from"),
                e.get("to"),
                e.get("time"),
                e.get("hashtags")
        );
    }

    public int insert(Map<String, Object> e, int requesterId) {
        String sql =
                "INSERT INTO errand (title, reward, from_place, to_place, time_text, status, hashtags, requester_id) " +
                        "VALUES (?, ?, ?, ?, ?, '모집중', ?, ?)";

        return jdbcTemplate.update(sql,
                e.get("title"),
                e.get("reward"),
                e.get("from"),
                e.get("to"),
                e.get("time"),
                e.get("hashtags"),
                requesterId
        );
    }


    public Map<String, Object> findById(int id) {
        String sql =
                "SELECT id, title, reward, " +
                        "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, requester_id AS requesterId, " +
                        "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS createdAt " +
                        "FROM errand WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    public ErrandVO findVOById(int id) {
        String sql =
                "SELECT id, title, reward, from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                        "status, hashtags, requester_id AS requesterId, created_at " +
                        "FROM errand WHERE id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                (rs, rowNum) -> {
                    ErrandVO e = new ErrandVO();
                    e.setId(rs.getInt("id"));
                    e.setTitle(rs.getString("title"));
                    e.setReward(rs.getInt("reward"));
                    e.setFrom(rs.getString("from"));
                    e.setTo(rs.getString("to"));
                    e.setTime(rs.getString("time"));
                    e.setStatus(rs.getString("status"));
                    e.setHashtagsRaw(rs.getString("hashtags"));
                    e.setRequesterId(rs.getInt("requesterId"));
                    e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return e;
                }
        );
    }
}


