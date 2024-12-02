package com.group4.controller;


import com.group4.entity.UserEntity;
import com.group4.service.IEmailService;
import com.group4.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Data
public class AuthController {
    @Autowired
    private final IUserService userService;
    @Autowired
    private final IEmailService emailSenderService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @RequestMapping(value = "register")
    public String addUser(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("user") UserEntity user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "register";
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("mess", "Email đã tồn tại. Hãy nhập Email mới!");
            return "register";
        }

        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);

        String subject = "Mã OTP đăng ký của bạn";
        String mess = "Chào bạn,\n\nMã OTP của bạn là: " + session.getAttribute("otp-register") + "\n\nVui lòng không chia sẻ mã này với bất kỳ ai.\n\nTrân trọng,\nNhóm hỗ trợ.";

        emailSenderService.sendEmail(user.getEmail(), subject, mess);

        session.setAttribute("email", user.getEmail());
        session.setAttribute("name", user.getName());
        session.setAttribute("password", user.getPassword());

        return "redirect:/otp-check";
    }


    @RequestMapping(value = "re-send")
    public String resend(HttpSession session) {
        session.removeAttribute("otp-register");
        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);

        String subject = "Mã OTP đăng ký của bạn";
        String mess = "Chào bạn,\n\nMã OTP của bạn là: " + session.getAttribute("otp-register") + "\n\nVui lòng không chia sẻ mã này với bất kỳ ai.\n\nTrân trọng,\nNhóm hỗ trợ.";

        emailSenderService.sendEmail((String) session.getAttribute("email"), subject, mess);

        return "redirect:/otp-check";
    }


    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }


}
