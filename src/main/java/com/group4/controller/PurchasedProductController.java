package com.group4.controller;

import com.group4.entity.UserEntity;
import com.group4.model.LineItemModel;
import com.group4.service.IPurchasedProductService;
import com.group4.service.impl.CustomerReviewServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/purchasedProduct")
public class PurchasedProductController {
    @Autowired
    private IPurchasedProductService iPurchasedProductService;
    @Autowired
    private CustomerReviewServiceImpl customerReviewServiceImpl;

    @GetMapping
    public String getPurchasedProducts(HttpSession session,@RequestParam Long orderId, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        List<LineItemModel> purchasedProducts = iPurchasedProductService.getAllLineItems(orderId);

        // Tạo Map để lưu trạng thái hasReviewed theo productID
        Map<Long, Boolean> hasReviewedMap = new HashMap<>();

        // Kiểm tra trạng thái đánh giá của từng sản phẩm
        purchasedProducts.forEach(product -> {
            boolean hasReviewed = customerReviewServiceImpl.hasReviewed(user.getUserID(), product.getProduct().getProductID());
            hasReviewedMap.put(product.getProduct().getProductID(), hasReviewed);
        });

        model.addAttribute("purchasedProducts", purchasedProducts);
        model.addAttribute("hasReviewedMap", hasReviewedMap); // Truyền Map vào Model
        model.addAttribute("orderId",orderId);
        return "purchasedProduct";
    }
}
