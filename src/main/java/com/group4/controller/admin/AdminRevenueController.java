package com.group4.controller.admin;


import com.group4.service.IRevenueService;
import com.group4.service.impl.RevenueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminRevenueController {
    @Autowired
    public IRevenueService revenueService = new RevenueServiceImpl();
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        return "dashboard";
    }

}
