package com.group4.controller;

import com.group4.model.AddressModel;
import com.group4.model.UserModel;
import com.group4.service.IAddressService;
import com.group4.service.IPersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/personal-info")
public class PersonalInfoController {

    @Autowired
    private IAddressService addressService ;
    @Autowired
    private IPersonalInfoService service;

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        return "error";  // Trang lỗi tùy chỉnh
    }
    // Lấy thông tin cá nhân và địa chỉ
    @GetMapping
    public String getPersonalInfo(HttpSession session,Model model) {
        Long userID = (Long) session.getAttribute("userID"); // Lấy userID từ session
        UserModel user = service.fetchPersonalInfo(userID); // Gọi Service để lấy thông tin người dùng
        if (user != null) {
            model.addAttribute("user", user); // Thêm thông tin người dùng vào model
        }
        return "personal-info"; // Hiển thị trang personal-info
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/profile")
    public String updatePersonalInfo(HttpSession session, UserModel userModel, Model model) {
        Long  userID= (Long) session.getAttribute("userID");
        userModel.setUserID(userID);
        boolean status = service.savePersonalInfo(userModel, userModel.getUserID()); // Lưu thông tin mới
        if (status) {
            model.addAttribute("message", "Cập nhật thông tin cá nhân thành công!");
            return "redirect:/personal-info?success"; // Chuyển hướng với trạng thái thành công
        } else {
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin cá nhân!");
            return "redirect:/personal-info?error"; // Chuyển hướng với trạng thái thất bại
        }
    }


    @PostMapping("/address")
    public String updateAddress(HttpSession session, @ModelAttribute AddressModel addressModel, Model model) {
        Long userID = (Long) session.getAttribute("userID"); // Lấy userID từ session
        UserModel user = service.fetchPersonalInfo(userID);

        AddressModel existingAddress = user.getAddress();
        if (existingAddress != null) {
            addressModel.setAddressID(existingAddress.getAddressID());
        }

        boolean status = addressService.updateAddressForUser(addressModel, addressModel.getAddressID());
        if (status) {
            model.addAttribute("message", "Cập nhật địa chỉ thành công!");
            return "redirect:/personal-info?address-success";
        } else {
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật địa chỉ!");
            return "redirect:/personal-info?address-error";
        }
    }


    @PostMapping("/password")
    public String changePassword(HttpSession session, String currentPassword, String newPassword, String confirmNewPassword, Model model) {
        Long userID = (Long) session.getAttribute("userID"); // Lấy userID từ session
        // Lấy thông tin người dùng từ database
        UserModel user = service.fetchPersonalInfo(userID);

        // Kiểm tra nếu không tìm thấy người dùng
        if (user == null) {
            model.addAttribute("error", "Người dùng không tồn tại!");
            return "redirect:/personal-info";
        }

        // Kiểm tra mật khẩu hiện tại
        if (!currentPassword.equals(user.getPassword())) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/personal-info";
        }

        // Kiểm tra mật khẩu mới có khớp với xác nhận mật khẩu không
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return "redirect:/personal-info";
        }

        // Cập nhật mật khẩu mới
        user.setPassword(newPassword);
        boolean status = service.savePersonalInfo(user, user.getUserID());

        if (status) {
            model.addAttribute("message", "Thay đổi mật khẩu thành công!");
            return "redirect:/personal-info?success";
        } else {
            model.addAttribute("error", "Có lỗi xảy ra khi thay đổi mật khẩu!");
            return "personal-info";
        }
    }
}
