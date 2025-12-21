package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/errand")
public class ErrandController {

    private final ErrandDAO errandDAO;

    public ErrandController(ErrandDAO errandDAO) {
        this.errandDAO = errandDAO;
    }

    /* ===================== 리스트 ===================== */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String category,
            Model model,
            HttpSession session
    ) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("errands", errandDAO.findAll(category));
        model.addAttribute("currentCategory", category);
        return "errand/list";
    }

    /* ===================== 글쓰기 폼 ===================== */
    @GetMapping("/create")
    public String createForm(HttpSession session) {
        if (session.getAttribute("loginMember") == null)
            return "redirect:/auth/login";
        return "errand/form";
    }

    /* ===================== 글 생성 ===================== */
    @PostMapping("/create")
    public String create(
            @RequestParam String title,
            @RequestParam int reward,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("time") String time,
            @RequestParam(required = false) String hashtags,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile[] images,
            HttpServletRequest request,
            HttpSession session
    ) throws Exception {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        // 1️⃣ 글 먼저 생성
        int errandId = errandDAO.insertAndReturnId(
                title, reward, from, to, time,
                hashtags, description, loginMember.getMemberId()
        );

        // 2️⃣ 이미지 저장
        saveImages(errandId, images, request);

        return "redirect:/errand/detail?id=" + errandId;
    }

    /* ===================== 상세 ===================== */
    @GetMapping("/detail")
    public String detail(@RequestParam int id, Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);

        int requesterId = ((Number) e.get("requesterId")).intValue();
        e.put("isMine", requesterId == loginMember.getMemberId());

        model.addAttribute("e", e);
        model.addAttribute("images", errandDAO.findImagesByErrandId(id));
        model.addAttribute("loginMember", loginMember);
        return "errand/detail";
    }

    /* ===================== 수정 폼 ===================== */
    @GetMapping("/edit")
    public String editForm(@RequestParam int id, Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);
        int requesterId = ((Number) e.get("requesterId")).intValue();
        if (requesterId != loginMember.getMemberId() && !"ADMIN".equals(loginMember.getRole()))
            return "redirect:/errand/detail?id=" + id;

        model.addAttribute("e", e);
        model.addAttribute("images", errandDAO.findImagesByErrandId(id));
        return "errand/edit";
    }

    /* ===================== 수정 처리 ===================== */
    @PostMapping("/edit")
    public String edit(
            @RequestParam int id,
            @RequestParam String title,
            @RequestParam int reward,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("time") String time,
            @RequestParam(required = false) String hashtags,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Integer> deleteImageIds,
            @RequestParam(required = false) MultipartFile[] images,
            HttpServletRequest request,
            HttpSession session
    ) throws Exception {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> origin = errandDAO.findById(id);
        int requesterId = ((Number) origin.get("requesterId")).intValue();
        if (requesterId != loginMember.getMemberId() && !"ADMIN".equals(loginMember.getRole()))
            return "redirect:/errand/detail?id=" + id;

        // 1️⃣ 본문 수정
        Map<String, Object> e = new HashMap<>();
        e.put("id", id);
        e.put("title", title);
        e.put("reward", reward);
        e.put("from", from);
        e.put("to", to);
        e.put("time", time);
        e.put("hashtags", hashtags);
        e.put("description", description);
        errandDAO.updateTextOnly(e);

        // 2️⃣ 이미지 삭제
        if (deleteImageIds != null) {
            for (int imageId : deleteImageIds) {
                errandDAO.deleteImageById(imageId);
            }
        }

        // 3️⃣ 새 이미지 추가
        saveImages(id, images, request);

        return "redirect:/errand/detail?id=" + id;
    }

    /* ===================== 삭제 ===================== */
    @PostMapping("/delete")
    public String delete(@RequestParam int id, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);
        int requesterId = ((Number) e.get("requesterId")).intValue();
        if (requesterId != loginMember.getMemberId() && !"ADMIN".equals(loginMember.getRole()))
            return "redirect:/errand/detail?id=" + id;

        errandDAO.deleteById(id);
        return "redirect:/errand/list";
    }

    /* ===================== 공통 이미지 저장 ===================== */
    private void saveImages(int errandId, MultipartFile[] images, HttpServletRequest request) throws Exception {
        if (images == null || images.length == 0) return;

        // ✅ 전부 assets/upload 한 폴더로 저장
        String uploadDir = request.getServletContext().getRealPath("/assets/upload");
        if (uploadDir == null) throw new IllegalStateException("getRealPath('/assets/upload') returned null");

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        List<String> fileNames = new ArrayList<>();

        for (MultipartFile mf : images) {
            if (mf == null || mf.isEmpty()) continue;

            String original = mf.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String savedName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;
            File dest = new File(dir, savedName);
            mf.transferTo(dest);

            // ✅ DB에는 savedName만 저장
            fileNames.add(savedName);
        }

        if (!fileNames.isEmpty()) {
            errandDAO.insertImages(errandId, fileNames);
        }
    }
}
