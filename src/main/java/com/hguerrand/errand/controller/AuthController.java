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

    /* =======================
       로그인
     ======================= */

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // 1️⃣ 아이디로 회원 조회
        MemberVO member = memberDAO.findByLoginId(loginId);

        // 아이디 없음
        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }

        // 2️⃣ 비밀번호 불일치
        if (!member.getPassword().equals(password)) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "auth/login";
        }

        // 3️⃣ 승인 대기
        if ("PENDING".equalsIgnoreCase(member.getStatus())) {
            model.addAttribute("error", "관리자 승인 대기중입니다.");
            return "auth/login";
        }

        // 4️⃣ 승인 거절 (선택)
        if ("REJECTED".equalsIgnoreCase(member.getStatus())) {
            model.addAttribute("error", "승인이 거절된 계정입니다.");
            return "auth/login";
        }

        // 5️⃣ 로그인 성공
        session.setAttribute("loginMember", member);
        return "redirect:/errand/list";
    }

    /* =======================
       로그아웃
     ======================= */

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    /* =======================
       회원가입
     ======================= */

    @GetMapping("/signup")
    public String signupForm() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String loginId,
                         @RequestParam String password,
                         @RequestParam String name,
                         @RequestParam("studentCard") MultipartFile studentCard,
                         HttpServletRequest request,
                         Model model) {

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

            // DB 저장 (PENDING 상태)
            memberDAO.insertSignup(
                    loginId,
                    password,
                    name,
                    "student/" + savedName
            );

            return "redirect:/auth/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "auth/signup";
        }
    }
}
