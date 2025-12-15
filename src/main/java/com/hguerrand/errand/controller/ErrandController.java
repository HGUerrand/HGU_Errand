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

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

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

        // 1) errand 먼저 insert 하고 생성된 id 받아야 함
        int errandId = errandDAO.insertAndReturnId(title, reward, from, to, time, hashtags, description, loginMember.getMemberId());

        // 2) 파일 저장 + errand_image insert
        if (images != null) {
            String uploadDir = request.getServletContext().getRealPath("/upload");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            List<String> fileNames = new ArrayList<>();
            for (MultipartFile mf : images) {
                if (mf == null || mf.isEmpty()) continue;

                String savedName = System.currentTimeMillis() + "_" + mf.getOriginalFilename();
                File dest = new File(uploadDir, savedName);
                mf.transferTo(dest);
                fileNames.add(savedName);
            }
            if (!fileNames.isEmpty()) errandDAO.insertImages(errandId, fileNames);
        }

        return "redirect:/errand/detail?id=" + errandId;
    }

    // ✅ 상세
    @GetMapping("/detail")
    public String detail(@RequestParam int id, Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);

        boolean isMine = false;
        Object requesterIdObj = e.get("requesterId");
        if (requesterIdObj != null) {
            int requesterId = ((Number) requesterIdObj).intValue();
            isMine = requesterId == loginMember.getMemberId();
        }
        e.put("isMine", isMine);

        // ✅ 이 줄이 핵심
        model.addAttribute("images", errandDAO.findImagesByErrandId(id));

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

    @GetMapping("/edit")
    public String editForm(@RequestParam int id, Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        Map<String, Object> e = errandDAO.findById(id);
        if (e == null) return "redirect:/errand/list?msg=notfound";

        int requesterId = ((Number) e.get("requesterId")).intValue();
        if (requesterId != loginMember.getMemberId()) {
            return "redirect:/errand/detail?id=" + id;
        }

        model.addAttribute("e", e);
        model.addAttribute("images", errandDAO.findImagesByErrandId(id)); // ✅ 추가

        return "errand/edit";
    }

    @PostMapping("/edit")
    public String edit(HttpServletRequest request, HttpSession session) throws Exception {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/auth/login";

        String uploadDir = request.getServletContext().getRealPath("/upload");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        int maxSize = 15 * 1024 * 1024;

        MultipartRequest multi = new MultipartRequest(
                request, uploadDir, maxSize, "UTF-8", new DefaultFileRenamePolicy()
        );

        int id = Integer.parseInt(multi.getParameter("id"));

        Map<String, Object> origin = errandDAO.findById(id);
        if (origin == null) return "redirect:/errand/list?msg=notfound";

        int requesterId = ((Number) origin.get("requesterId")).intValue();
        if (requesterId != loginMember.getMemberId()) {
            return "redirect:/errand/detail?id=" + id;
        }

        // 1) 글 본문 업데이트
        Map<String, Object> e = new HashMap<>();
        e.put("id", id);
        e.put("title", multi.getParameter("title"));
        e.put("reward", Integer.parseInt(multi.getParameter("reward")));
        e.put("from", multi.getParameter("from"));
        e.put("to", multi.getParameter("to"));
        e.put("time", multi.getParameter("time"));
        e.put("hashtags", multi.getParameter("hashtags"));
        e.put("description", multi.getParameter("description"));

        errandDAO.updateTextOnly(e); // ✅ 아래 DAO에서 분리 추천

        // 2) 삭제할 이미지 id들
        String[] deleteIds = multi.getParameterValues("deleteImageIds");
        if (deleteIds != null) {
            for (String s : deleteIds) {
                int imageId = Integer.parseInt(s);
                // (선택) 파일명 조회해서 실제 파일도 삭제하려면 getImagePathById 필요
                errandDAO.deleteImageById(imageId);
            }
        }

        // 3) 새로 업로드한 이미지들 추가
        List<String> newFiles = new ArrayList<>();
        Enumeration<String> files = multi.getFileNames();
        while (files.hasMoreElements()) {
            String inputName = files.nextElement(); // images 여러개
            String saved = multi.getFilesystemName(inputName);
            if (saved != null && !saved.trim().isEmpty()) newFiles.add(saved);
        }
        if (!newFiles.isEmpty()) {
            errandDAO.insertImages(id, newFiles);
        }

        return "redirect:/errand/detail?id=" + id;
    }


}