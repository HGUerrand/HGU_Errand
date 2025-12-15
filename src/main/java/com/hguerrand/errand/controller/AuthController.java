// src/main/java/com/hguerrand/errand/controller/AuthController.java
package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final MemberDAO memberDAO;

    public AuthController(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        System.out.println("[LOGIN] id=" + loginId + ", pw=" + password);

        MemberVO member = memberDAO.findByLoginIdAndPassword(loginId, password);

        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }

        if (!"APPROVED".equalsIgnoreCase(member.getStatus())) {
            model.addAttribute("error", "승인 대기중입니다. (상태: " + member.getStatus() + ")");
            return "auth/login";
        }

        session.setAttribute("loginMember", member);
        System.out.println("[LOGIN] success, session set: " + session.getAttribute("loginMember"));
        return "redirect:/errand/list";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
