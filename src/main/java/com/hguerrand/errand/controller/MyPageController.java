package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final ErrandDAO errandDAO;
    private final MemberDAO memberDAO;

    public MyPageController(ErrandDAO errandDAO, MemberDAO memberDAO) {
        this.errandDAO = errandDAO;
        this.memberDAO = memberDAO;
    }

    @GetMapping
    public String mypage(Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> me = memberDAO.findById(loginMember.getMemberId());

        model.addAttribute("me", me);
        model.addAttribute("myErrands", errandDAO.findByRequesterId(loginMember.getMemberId()));
        return "mypage/index";
    }

    @PostMapping("/name")
    public String updateName(@RequestParam String name, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) return "redirect:/mypage?toast=name"; // 빈값 방지 원하면 여기서 처리

        memberDAO.updateName(loginMember.getMemberId(), trimmed);

        // 세션에도 반영(안 하면 일부 화면이 옛 세션값 쓸 때 mismatch 날 수 있음)
        loginMember.setName(trimmed);
        session.setAttribute("loginMember", loginMember);

        return "redirect:/mypage?toast=name";
    }

    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                               HttpServletRequest request,
                               HttpSession session) throws Exception {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        if (avatarFile == null || avatarFile.isEmpty()) {
            return "redirect:/mypage?toast=avatar";
        }

        // ✅ 전부 assets/upload 한 폴더로 저장
        String uploadDir = request.getServletContext().getRealPath("/assets/upload");
        if (uploadDir == null) throw new IllegalStateException("getRealPath('/assets/upload') returned null");

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // ✅ 안전한 파일명 (확장자 유지 + UUID)
        String original = avatarFile.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String savedName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID() + ext;

        File dest = new File(dir, savedName);
        avatarFile.transferTo(dest);

        // ✅ DB에는 savedName만 저장
        memberDAO.updateAvatar(loginMember.getMemberId(), savedName);

        // ✅ 세션 반영
        loginMember.setAvatar(savedName);
        session.setAttribute("loginMember", loginMember);

        return "redirect:/mypage?toast=avatar";
    }
}