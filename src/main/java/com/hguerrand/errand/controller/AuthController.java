// src/main/java/com/hguerrand/errand/controller/AuthController.java
package com.hguerrand.errand.controller;

import com.hguerrand.errand.util.GoogleTokenVerifier;
import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final MemberDAO memberDAO;

    public AuthController(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    /* =======================
       로그인 GET
     ======================= */
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            Model model) {

        if ("invalid".equals(error)) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
        } else if ("notApproved".equals(error)) {
            model.addAttribute("errorMsg", "관리자 승인 대기중입니다.");
        } else if ("loginRequired".equals(error)) {
            model.addAttribute("errorMsg", "로그인이 필요합니다.");
        }

        return "auth/login";
    }

    /* =======================
       기존 로그인
     ======================= */
    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session) {

        MemberVO member = memberDAO.findByLoginId(loginId);

        if (member == null || !member.getPassword().equals(password)) {
            return "redirect:/auth/login?error=invalid";
        }

        if (!"APPROVED".equalsIgnoreCase(member.getStatus())) {
            return "redirect:/auth/login?error=notApproved";
        }

        session.setAttribute("loginMember", member);
        return "redirect:/errand/list";
    }

    /* =======================
       Google 로그인
     ======================= */
    @PostMapping("/google")
    @ResponseBody
    public Map<String, Object> googleLogin(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        Map<String, Object> result = new HashMap<>();

        String credential = body.get("credential");
        if (credential == null) {
            result.put("success", false);
            result.put("message", "credential 없음");
            return result;
        }

        String email = GoogleTokenVerifier.verifyAndGetEmail(credential);

        if (email == null) {
            result.put("success", false);
            result.put("message", "Google 토큰 검증 실패");
            return result;
        }

        // handong.ac.kr 제한
        if (!email.endsWith("@handong.ac.kr")) {
            result.put("success", false);
            result.put("message", "handong.ac.kr 계정만 로그인 가능합니다.");
            return result;
        }

        MemberVO member = memberDAO.findByLoginId(email);

        if (member == null) {
            memberDAO.insertGoogleUser(email);
            member = memberDAO.findByLoginId(email); // 다시 조회
        }

        session.setAttribute("loginMember", member);
        result.put("success", true);
        return result;
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

        if (studentCard == null || studentCard.isEmpty()) {
            model.addAttribute("error", "학생증 사진은 필수입니다.");
            return "auth/signup";
        }

        try {
            String uploadDir =
                    request.getServletContext().getRealPath("/upload/student");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String savedName =
                    System.currentTimeMillis() + "_" + studentCard.getOriginalFilename();
            File dest = new File(uploadDir, savedName);
            studentCard.transferTo(dest);

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
