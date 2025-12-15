package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberDAO memberDAO;

    public AdminController(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    // 관리자 권한 체크
    private boolean isAdmin(HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        return loginMember != null && "ADMIN".equals(loginMember.getRole());
    }

    // 승인 대기 목록
    @GetMapping("/members")
    public String pendingMembers(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/auth/login";

        model.addAttribute("members", memberDAO.findPendingMembers());
        return "admin/members";
    }

    // 승인 처리
    @PostMapping("/approve")
    public String approve(@RequestParam int memberId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";

        memberDAO.approveMember(memberId);
        return "redirect:/admin/members";
    }
}
