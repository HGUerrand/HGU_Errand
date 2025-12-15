package com.hguerrand.errand.dao;

import com.hguerrand.errand.vo.MemberVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberDAO {

    private final JdbcTemplate jdbcTemplate;

    public MemberDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MemberVO findByLoginIdAndPassword(String loginId, String password) {
        String sql = "SELECT member_id, login_id, password, name, role, status " +
                "FROM member WHERE login_id = ? AND password = ?";

        List<MemberVO> list = jdbcTemplate.query(sql, new Object[]{loginId, password}, (rs, rowNum) -> {
            MemberVO m = new MemberVO();
            m.setMemberId(rs.getInt("member_id"));
            m.setLoginId(rs.getString("login_id"));
            m.setPassword(rs.getString("password"));
            m.setName(rs.getString("name"));
            m.setRole(rs.getString("role"));
            m.setStatus(rs.getString("status"));
            return m;
        });

        return list.isEmpty() ? null : list.get(0);
    }
}
