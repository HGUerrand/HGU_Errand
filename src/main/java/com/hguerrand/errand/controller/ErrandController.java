package com.hguerrand.errand.controller;

import com.hguerrand.errand.dao.ErrandDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errand")
public class ErrandController {

    private final ErrandDAO errandDAO;

    public ErrandController(ErrandDAO errandDAO) {
        this.errandDAO = errandDAO;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("errands", errandDAO.findAllAsMap());
        return "errand/list";
    }
}