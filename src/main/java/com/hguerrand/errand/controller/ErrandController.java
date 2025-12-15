package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/errand")
public class ErrandController {

    private final ErrandDAO errandDAO;

    public ErrandController(ErrandDAO errandDAO) {
        this.errandDAO = errandDAO;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("errands", errandDAO.findAll());
        return "errand/list";
    }

    @GetMapping("/create")
    public String createForm() {
        return "errand/form";
    }

    /**
     * ✅ 사진 업로드 + description + writerName 포함 생성
     * form.jsp에서 enctype="multipart/form-data" 필수
     */
    @PostMapping("/create")
    public String create(HttpServletRequest request) throws Exception {

        // ✅ upload 실제 경로 (webapp/upload)
        String uploadDir = request.getServletContext().getRealPath("/upload");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        int maxSize = 15 * 1024 * 1024; // 15MB

        MultipartRequest multi = new MultipartRequest(
                request,
                uploadDir,
                maxSize,
                "UTF-8",
                new DefaultFileRenamePolicy()
        );

        // multipart에서는 request.getParameter() 말고 multi.getParameter() 써야 함
        String title = multi.getParameter("title");
        int reward = Integer.parseInt(multi.getParameter("reward"));
        String from = multi.getParameter("from");
        String to = multi.getParameter("to");
        String time = multi.getParameter("time");
        String hashtags = multi.getParameter("hashtags");
        String description = multi.getParameter("description");
        String writerName = multi.getParameter("writerName");

        // 업로드 파일명 (없으면 null)
        String imageFileName = multi.getFilesystemName("image");

        Map<String, Object> e = new HashMap<>();
        e.put("title", title);
        e.put("reward", reward);
        e.put("from", from);
        e.put("to", to);
        e.put("time", time);
        e.put("hashtags", hashtags);
        e.put("description", description);

        e.put("imagePath", imageFileName); // DB image_path에 저장
        e.put("writerName", (writerName == null || writerName.trim().isEmpty()) ? "익명" : writerName);
        e.put("writerAvatar", "default.png"); // 일단 기본 프사

        errandDAO.insert(e);

        return "redirect:/errand/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam int id, Model model) {
        Map<String, Object> e = errandDAO.findById(id);

        // ✅ 테스트용: 작성자 이름이 "정다연"이면 내 글 처리 (원하는 기준으로 바꿔도 됨)
        String writerName = (String) e.get("writer_name");
        boolean isMine = "정다연".equals(writerName);

        e.put("isMine", isMine);

        model.addAttribute("e", e);
        return "errand/detail";
    }

    /**
     * ✅ 상태 변경 (모집중->예약->완료)
     * detail.jsp에서 form post로 호출
     */
    @PostMapping("/status")
    public String updateStatus(@RequestParam int id, @RequestParam String status) {
        errandDAO.updateStatus(id, status);
        return "redirect:/errand/detail?id=" + id;
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam int id, Model model) {
        model.addAttribute("e", errandDAO.findById(id));
        return "errand/edit";
    }

    @PostMapping("/edit")
    public String edit(HttpServletRequest request) throws Exception {

        String uploadDir = request.getServletContext().getRealPath("/upload");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        int maxSize = 15 * 1024 * 1024;

        MultipartRequest multi = new MultipartRequest(
                request, uploadDir, maxSize, "UTF-8", new DefaultFileRenamePolicy()
        );

        int id = Integer.parseInt(multi.getParameter("id"));
        String title = multi.getParameter("title");
        int reward = Integer.parseInt(multi.getParameter("reward"));
        String from = multi.getParameter("from");
        String to = multi.getParameter("to");
        String time = multi.getParameter("time");
        String hashtags = multi.getParameter("hashtags");
        String description = multi.getParameter("description");
        String writerName = multi.getParameter("writerName");

        // ✅ 새 이미지 업로드 안 하면 null
        String newImage = multi.getFilesystemName("image");
        // ✅ 기존 이미지 파일명(hidden) 받아오기
        String oldImage = multi.getParameter("oldImage");

        String finalImage = (newImage != null) ? newImage : oldImage;

        Map<String, Object> e = new HashMap<>();
        e.put("id", id);
        e.put("title", title);
        e.put("reward", reward);
        e.put("from", from);
        e.put("to", to);
        e.put("time", time);
        e.put("hashtags", hashtags);
        e.put("description", description);
        e.put("writerName", (writerName == null || writerName.trim().isEmpty()));
        e.put("imagePath", finalImage);

        errandDAO.update(e);

        return "redirect:/errand/detail?id=" + id;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int id, HttpServletRequest request) {

        // ✅ e.isMine을 만드는 기준이랑 똑같이 "내 글" 판별 (테스트용)
        Map<String, Object> e = errandDAO.findById(id);
        String writerName = (String) e.get("writer_name");
        boolean isMine = "정다연".equals(writerName); // 너 기준에 맞게 바꿔도 됨

        if (!isMine) {
            // 남의 글 삭제 시도 차단
            return "redirect:/errand/detail?id=" + id;
        }

        errandDAO.deleteById(id);
        return "redirect:/errand/list";
    }

    @Controller
    public class MyPageController {
        @GetMapping("/mypage")
        public String mypage() {
            return "mypage/index"; // 일단 빈 화면/준비중
        }
    }
}