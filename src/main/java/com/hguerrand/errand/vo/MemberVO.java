package com.hguerrand.errand.vo;

import java.time.LocalDateTime;

public class MemberVO {

    private int memberId;        // PK
    private String loginId;      // 로그인 아이디
    private String password;     // 비밀번호
    private String name;         // 이름(닉네임)
    private String avatar;   // ✅ 추가
    private String role;         // USER / ADMIN
    private String status;       // PENDING / APPROVED
    private LocalDateTime createdAt;


    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ✅ 추가 getter/setter
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
