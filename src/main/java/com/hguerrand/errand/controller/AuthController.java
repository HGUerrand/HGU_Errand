// src/main/java/com/hguerrand/errand/controller/AuthController.java
package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;

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

    @PostMapping("/signup")
    public String signup(
            @RequestParam String loginId,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam("studentCard") MultipartFile studentCard,
            HttpServletRequest request,
            Model model
    ) {

    /*
    // 학교 이메일 제한
    if (!loginId.endsWith("@handong.ac.kr")) {
        model.addAttribute("error", "학교 이메일만 가입 가능합니다.");
        return "auth/signup";
    }
    */

        // 학생증 필수
        if (studentCard == null || studentCard.isEmpty()) {
            model.addAttribute("error", "학생증 사진은 필수입니다.");
            return "auth/signup";
        }

        try {
            // 업로드 경로
            String uploadDir =
                    request.getServletContext().getRealPath("/upload/student");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 파일 저장
            String savedName =
                    System.currentTimeMillis() + "_" + studentCard.getOriginalFilename();
            File dest = new File(uploadDir, savedName);
            studentCard.transferTo(dest);

            // DB 저장 (승인 대기 상태)
            memberDAO.insertSignup(
                    loginId,
                    password,
                    name,
                    "student/" + savedName
            );

            // 로그인 페이지로 이동
            return "redirect:/auth/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "auth/signup";
        }
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "auth/signup";
    }


}
