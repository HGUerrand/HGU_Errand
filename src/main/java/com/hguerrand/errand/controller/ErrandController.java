package com.hguerrand.errand.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/errand")
public class ErrandController {

    @GetMapping("/list")
    public String list(Model model) {
        List<Map<String, Object>> errands = new ArrayList<>();

        errands.add(new HashMap<String, Object>() {{
            put("id", 1);
            put("title", "편의점에서 물 2개만 사다주실 분");
            put("reward", 2000);
            put("from", "학생회관");
            put("to", "오석관");
            put("time", "오늘 19:00");
            put("status", "모집중"); // 모집중이면 우측 상단 pill 없음
            put("createdAt", "10분 전");
            put("hashtags", Arrays.asList("#급함", "#가벼움"));
        }});

        errands.add(new HashMap<String, Object>() {{
            put("id", 2);
            put("title", "프린트물 대신 뽑아주기");
            put("reward", 1500);
            put("from", "도서관");
            put("to", "느헤미야홀");
            put("time", "내일 10:30");
            put("status", "예약"); // 우측 상단 pill: 예약
            put("createdAt", "3일 전");
            put("hashtags", Arrays.asList("#가벼움"));
        }});

        errands.add(new HashMap<String, Object>() {{
            put("id", 3);
            put("title", "택배 픽업 부탁!");
            put("reward", 3000);
            put("from", "정문");
            put("to", "기숙사");
            put("time", "오늘 21:00");
            put("status", "완료"); // 우측 상단 pill: 완료 + 카드 연해짐
            put("createdAt", "8일 전");
            put("hashtags", Arrays.asList("#급함", "#거리있음"));
        }});

        model.addAttribute("errands", errands);
        return "errand/list";
    }

    @GetMapping("/detail")
    public String detail() {
        return "errand/detail";
    }
}