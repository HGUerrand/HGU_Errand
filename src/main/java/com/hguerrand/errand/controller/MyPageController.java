package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import com.hguerrand.errand.dao.MemberDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
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

        // 최신 member 정보 다시 조회(닉네임/프사 컬럼을 나중에 추가할 거라 대비)
        Map<String, Object> me = memberDAO.findById(loginMember.getMemberId());

        model.addAttribute("me", me);
        model.addAttribute("myErrands", errandDAO.findByRequesterId(loginMember.getMemberId()));
        return "mypage/index";
    }
}