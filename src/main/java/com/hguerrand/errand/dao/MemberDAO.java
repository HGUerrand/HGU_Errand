package com.hguerrand.errand.dao;

import com.hguerrand.errand.vo.MemberVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    public Map<String, Object> findById(int memberId) {
        String sql =
                "SELECT member_id, login_id, name, role, status, avatar AS avatar, created_at " +
                        "FROM member WHERE member_id=?";
        return jdbcTemplate.queryForMap(sql, memberId);
    }

    // ✅ 닉네임 변경
    public int updateName(int memberId, String name) {
        return jdbcTemplate.update("UPDATE member SET name=? WHERE member_id=?", name, memberId);
    }

    // ✅ 아바타 변경
    public int updateAvatar(int memberId, String avatar) {
        return jdbcTemplate.update("UPDATE member SET avatar=? WHERE member_id=?", avatar, memberId);
    }

    public int insertSignup(String loginId, String password, String name, String studentCardPath) {
        String sql =
                "INSERT INTO member " +
                        "(login_id, password, name, role, status, student_card_path) " +
                        "VALUES (?, ?, ?, 'USER', 'PENDING', ?)";

        return jdbcTemplate.update(
                sql,
                loginId,
                password,
                name,
                studentCardPath
        );
    }
}
