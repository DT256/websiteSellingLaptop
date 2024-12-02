package com.group4.controller;

import com.group4.entity.CustomerEntity;
import com.group4.entity.UserEntity;
import com.group4.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("")
public class LoginController {
    @Autowired
    private IUserService userService;


    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        boolean isValidCredentials = userService.validateCredentials(username, password);
        if (isValidCredentials) {
            UserEntity user = userService.findByEmail(username).get();
            session.setAttribute("username", username);
            session.setAttribute("fullName", user.getName());
            session.setAttribute("roleName", user.getRoleName());
            session.setAttribute("user", user);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Sai email hoặc mật khẩu!");
            return "login";
        }
    }

}

