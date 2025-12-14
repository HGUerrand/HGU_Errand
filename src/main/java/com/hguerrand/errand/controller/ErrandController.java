package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public String create(
            @RequestParam String title,
            @RequestParam int reward,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("time") String time,
            @RequestParam(required = false) String hashtags
    ) {
        Map<String, Object> e = new HashMap<>();
        e.put("title", title);
        e.put("reward", reward);
        e.put("from", from);
        e.put("to", to);
        e.put("time", time);
        e.put("hashtags", hashtags); // "#급함,#가벼움" 형태로 받기

        errandDAO.insert(e);
        return "redirect:/errand/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam int id, Model model) {
        model.addAttribute("e", errandDAO.findById(id));
        return "errand/detail";
    }
}