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

    public List<Map<String, Object>> findAllAsMap() {
        String sql = "SELECT id, title, reward, " +
                "from_place AS `from`, to_place AS `to`, time_text AS `time`, " +
                "status, hashtags, created_at " +
                "FROM errand ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
    }
}