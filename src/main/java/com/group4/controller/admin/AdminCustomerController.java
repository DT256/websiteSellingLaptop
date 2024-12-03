package com.group4.controller.admin;

import com.group4.entity.CustomerEntity;
import com.group4.entity.OrderEntity;
import com.group4.repository.CustomerRepository;
import com.group4.service.ICustomerService;
import com.group4.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String listCustomers(Model model) {
        List<CustomerEntity> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customer-list";
    }

    // Tìm kiếm khách hàng
    @GetMapping("/search")
    public String searchCustomers(@RequestParam("keyword") String keyword, Model model) {
        List<CustomerEntity> customers = customerService.searchCustomers(keyword); // Cần cài đặt phương thức search
        model.addAttribute("customers", customers);
        return "customer-list";
    }

    @GetMapping("/{id}")
    public String getCustomerDetails(@PathVariable Long id, Model model) {

        // Lấy thông tin khách hàng
        CustomerEntity customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        model.addAttribute("customer", customer);

        // Lấy danh sách lịch sử đơn hàng
        List<OrderEntity> orders = orderService.getOrdersByCustomerId(id);
        model.addAttribute("orders", orders);

        return "customer-details";
    }

    // khóa tài khoản
    @GetMapping("/{id}/lock")
    public String lockCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
                customerService.updateActiveStatus(id, false); // Khóa tài khoản
            redirectAttributes.addFlashAttribute("successMessage", "Khóa thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Khoá không thành công: " + e.getMessage());
        }
        return "redirect:/admin/customers"; // Quay lại danh sách khách hàng
    }

    // mở khóa tài khoản
    @GetMapping("/{id}/unlock")
    public String unlockCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.updateActiveStatus(id, true); // Mở khóa tài khoả
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Mở khoá không thành công: " + e.getMessage());
        }
        return "redirect:/admin/customers"; // Quay lại danh sách khách hàng
    }
}
