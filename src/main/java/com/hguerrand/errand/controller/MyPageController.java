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

        String uploadDir = request.getServletContext().getRealPath("/upload");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String savedName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
        File dest = new File(uploadDir, savedName);
        avatarFile.transferTo(dest);

        memberDAO.updateAvatar(loginMember.getMemberId(), savedName);

        // 세션에도 반영
        loginMember.setAvatar(savedName);
        session.setAttribute("loginMember", loginMember);

        return "redirect:/mypage?toast=avatar";
    }
}