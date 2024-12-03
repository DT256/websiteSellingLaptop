package com.group4.controller;


import com.group4.entity.OrderEntity;
import com.group4.entity.UserEntity;
import com.group4.service.IHistoryService;
import jakarta.servlet.http.HttpSession;
import com.group4.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private IHistoryService historyService;

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public String viewOrderHistory(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            // Nếu user chưa đăng nhập, hiển thị thông báo
            model.addAttribute("message", "Bạn cần đăng nhập để xem lịch sử đơn hàng.");
            return "login"; // Điều hướng đến trang đăng nhập
        }

        // Gọi service để lấy danh sách đơn hàng của user
        Long userID = user.getUserID();
        List<OrderEntity> orders = historyService.getPurchaseHistory(userID);
        if (orders.isEmpty()) {
            model.addAttribute("message", "Bạn chưa mua đơn hàng nào.");
        } else {
            model.addAttribute("orders", orders);
        }
        return "my-account";
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId,
                              @RequestParam("bankName") String bankName,
                              @RequestParam("accountName") String accountName,
                              @RequestParam("accountNumber") String accountNumber,
                              Model model) {

        OrderEntity order = historyService.getOrderById(orderId);

        if (order == null) {
            model.addAttribute("errorMessage", "Đơn hàng không tồn tại.");
            return "redirect:/history";
        }

        // Kiểm tra trạng thái đơn hàng
        if ("Đã giao".equals(order.getShippingStatus()) || "Đang giao".equals(order.getShippingStatus())) {
            model.addAttribute("errorMessage", "Hủy đơn hàng không thành công.");
            return "redirect:/history";
        }

        // Hủy đơn hàng
        historyService.cancelOrder(orderId, accountNumber, accountName, bankName);

        model.addAttribute("successMessage", "Hủy đơn hàng thành công.");
        return "redirect:/history";
    }
}
