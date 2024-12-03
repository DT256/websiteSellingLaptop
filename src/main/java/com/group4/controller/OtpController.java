package com.group4.controller;


import com.group4.config.Constants;
import com.group4.entity.CustomerEntity;
import com.group4.repository.AddressRepository;
import com.group4.repository.CustomerRepository;
import com.group4.repository.UserRepository;
import com.group4.service.IEmailService;
import com.group4.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class OtpController {
    @Autowired
    private final IUserService userService;
    @Autowired
    private final IEmailService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;
    Constants constants = new Constants();
    String email;

    public OtpController(IUserService userService, IEmailService emailSenderService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "auth/otpConfirm";
    }


    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model) {
        String otpRegister = (String) session.getAttribute("otp-register");
        if (otp.equals(otpRegister)) {

            String fullName = (String) session.getAttribute("name");
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setEmail((String) session.getAttribute("email"));
            customerEntity.setPassword((String) session.getAttribute("password"));

            customerEntity.setName(fullName);
            customerEntity.setActive(true);

            customerEntity = customerRepository.save(customerEntity);


            customerRepository.save(customerEntity);
            return "redirect:/home";
        }
        model.addAttribute("mess","OTP is not correct! Please check your email.");
        return "auth/otpConfirm";
    }




    @RequestMapping(value = "recover", method = RequestMethod.GET)
    public String recover() {
        return "auth/recoverPage";
    }

    @RequestMapping(value = "send-otp-recover", method = RequestMethod.POST)
    public String getMail(@RequestParam("emailaddress") String email, HttpSession session, Model model) {

        if(!userRepository.existsByEmail(email)) {
            model.addAttribute("mess", "Email không tồn tại trong hệ thống!");
            return "auth/recoverPage";
        }

        session.setAttribute("emailToReset", email);

        String otpCode = constants.otpCode();

        String subject = "Mã OTP khôi phục tài khoản";
        String mess = "Chào bạn,\n\nMã OTP của bạn là: " + otpCode + "\n\nVui lòng không chia sẻ mã này với bất kỳ ai.\n\nTrân trọng,\nNhóm hỗ trợ.";

        this.emailSenderService.sendEmail(email, subject, mess);

        session.setAttribute("recoverOtp", otpCode);
        session.setMaxInactiveInterval(360);

        return "auth/confirmOtpRecover";
    }


    @RequestMapping(value = "confirm-otp-recover", method = RequestMethod.POST)
    public String recover( @RequestParam("otp") String otp, Model model,HttpSession session) {
        if (session.getAttribute("recoverOtp").equals(otp)){
            return "auth/confirmNewPassword";
        }
        model.addAttribute("mess","OTP is not correct! Please check your email.");
        return "auth/confirmOtpRecover";
    }

    @RequestMapping(value = "save-new-password", method = RequestMethod.POST)
    public String saveNewPassword(@RequestParam("password") String password, HttpSession session) {
        String email = (String) session.getAttribute("emailToReset");
        userService.recoverPassword(password,email);
        return "redirect:/login";
    }

}
