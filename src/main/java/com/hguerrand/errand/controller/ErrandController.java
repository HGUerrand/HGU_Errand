package com.hguerrand.errand.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errand")
public class ErrandController {
    @GetMapping("/list")
    public String list() {
        return "errand/list";
    }
}