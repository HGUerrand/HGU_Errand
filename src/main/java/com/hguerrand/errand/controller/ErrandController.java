package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import javax.servlet.http.HttpServletRequest;

import java.io.File;

@Controller
@RequestMapping("/errand")
public class ErrandController {

    private final ErrandDAO errandDAO;

    public ErrandController(ErrandDAO errandDAO) {
        this.errandDAO = errandDAO;
    }

    // ✅ 리스트
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        model.addAttribute("loginMember", loginMember); // jsp에서 필요하면 씀
        model.addAttribute("errands", errandDAO.findAll());
        return "errand/list";
    }

    // ✅ 글쓰기 폼
    @GetMapping("/create")
    public String createForm(HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        return "errand/form";
    }

    // ✅ 글 생성 (requester_id = loginMember.memberId 저장)
    @PostMapping("/create")
    public String create(HttpServletRequest request, HttpSession session) throws Exception {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        // 1️⃣ 업로드 경로
        String uploadDir = request.getServletContext().getRealPath("/upload");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        int maxSize = 15 * 1024 * 1024; // 15MB

        // 2️⃣ MultipartRequest 생성 (여기서 파싱됨)
        MultipartRequest multi = new MultipartRequest(
                request,
                uploadDir,
                maxSize,
                "UTF-8",
                new DefaultFileRenamePolicy()
        );

        // 3️⃣ 파라미터는 multi에서 꺼내야 함 ❗
        String title = multi.getParameter("title");
        int reward = Integer.parseInt(multi.getParameter("reward"));
        String from = multi.getParameter("from");
        String to = multi.getParameter("to");
        String time = multi.getParameter("time");
        String hashtags = multi.getParameter("hashtags");
        String description = multi.getParameter("description");

        // 4️⃣ 파일
        String imageFileName = multi.getFilesystemName("image"); // 없으면 null

        Map<String, Object> e = new HashMap<>();
        e.put("title", title);
        e.put("reward", reward);
        e.put("from", from);
        e.put("to", to);
        e.put("time", time);
        e.put("hashtags", hashtags);
        e.put("description", description);
        e.put("imagePath", imageFileName);

        errandDAO.insert(e, loginMember.getMemberId());

        return "redirect:/errand/list";
    }

    // ✅ 상세
    @GetMapping("/detail")
    public String detail(@RequestParam int id, Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);

        // 내 글 판별: requesterId == loginMember.memberId
        boolean isMine = false;
        Object requesterIdObj = e.get("requesterId");
        if (requesterIdObj != null) {
            int requesterId = ((Number) requesterIdObj).intValue();
            isMine = requesterId == loginMember.getMemberId();
        }
        e.put("isMine", isMine);

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("e", e);
        return "errand/detail";
    }

    // ✅ 상태 변경 (내 글만 가능)
    // - 모집중/예약/완료/모집중(완료취소) 등
    @PostMapping("/status")
    public String updateStatus(@RequestParam int id, @RequestParam String status, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        // 권한 체크
        Map<String, Object> e = errandDAO.findById(id);
        Object requesterIdObj = e.get("requesterId");
        if (requesterIdObj == null) return "redirect:/errand/detail?id=" + id;

        int requesterId = ((Number) requesterIdObj).intValue();
        if (requesterId != loginMember.getMemberId()) {
            // 남의 글 상태 변경 차단
            return "redirect:/errand/detail?id=" + id;
        }

        errandDAO.updateStatus(id, status);
        return "redirect:/errand/detail?id=" + id;
    }

    // ✅ 삭제 (내 글만 가능)
    @PostMapping("/delete")
    public String delete(@RequestParam int id, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);
        Object requesterIdObj = e.get("requesterId");
        if (requesterIdObj == null) return "redirect:/errand/detail?id=" + id;

        int requesterId = ((Number) requesterIdObj).intValue();
        if (requesterId != loginMember.getMemberId()) {
            return "redirect:/errand/detail?id=" + id;
        }

        errandDAO.deleteById(id);
        return "redirect:/errand/list";
    }
}