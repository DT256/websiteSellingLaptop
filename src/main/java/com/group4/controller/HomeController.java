package com.group4.controller;


import ch.qos.logback.core.model.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String index(){
        return "home";
    }

    @GetMapping("/inventory")
    public String inventory(){
        return "inventory";
    }
}
