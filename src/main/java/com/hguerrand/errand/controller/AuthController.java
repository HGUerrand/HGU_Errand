// src/main/java/com/hguerrand/errand/controller/AuthController.java
package com.hguerrand.errand.controller;
import com.hguerrand.errand.util.GoogleTokenVerifier;

import java.util.HashMap;
import java.util.Map;


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
       로그인 GET (메시지 출력 담당)
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


    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session) {

        MemberVO member = memberDAO.findByLoginId(loginId);

        System.out.println("=== 로그인 시도 시작 ===");
        System.out.println("아이디: " + loginId);
        if (member != null) {
            System.out.println("DB에서 가져온 상태값(status): [" + member.getStatus() + "]");
        } else {
            System.out.println("해당 아이디의 사용자를 찾을 수 없음");
        }

        if (member == null || !member.getPassword().equals(password)) {
            return "redirect:/auth/login?error=invalid";
        }

        if (!"APPROVED".equalsIgnoreCase(member.getStatus())) {
            System.out.println("결과: 승인되지 않은 계정이라 리다이렉트합니다.");
            return "redirect:/auth/login?error=notApproved";
        }

        System.out.println("결과: 승인된 계정이라 로그인을 허용합니다.");
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

    @PostMapping("/google")
    @ResponseBody
    public Map<String, Object> googleLogin(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        Map<String, Object> result = new HashMap<>();

        try {
            String credential = body.get("credential");
            if (credential == null) {
                result.put("success", false);
                result.put("message", "Google credential 없음");
                return result;
            }

            // 🔐 Google 토큰 검증
            String email = GoogleTokenVerifier.verifyAndGetEmail(credential);

            if (email == null) {
                result.put("success", false);
                result.put("message", "Google 토큰 검증 실패");
                return result;
            }

            // 🔥 handong.ac.kr 도메인 제한
            if (!email.endsWith("@handong.ac.kr")) {
                result.put("success", false);
                result.put("message", "handong.ac.kr 계정만 로그인 가능합니다.");
                return result;
            }

            // DB에서 회원 조회
            MemberVO member = memberDAO.findByLoginId(email);

            if (member == null) {
                // ❗ 처음 로그인 → 자동 회원 생성 (PENDING)
                memberDAO.insertGoogleUser(email);
                result.put("success", false);
                result.put("message", "회원 가입 후 관리자 승인이 필요합니다.");
                return result;
            }

            if (!"APPROVED".equalsIgnoreCase(member.getStatus())) {
                result.put("success", false);
                result.put("message", "관리자 승인 대기중입니다.");
                return result;
            }

            // ✅ 로그인 성공
            session.setAttribute("loginMember", member);
            result.put("success", true);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Google 로그인 실패");
            return result;
        }
    }


}
