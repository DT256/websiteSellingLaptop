package com.group4.controller;

import com.group4.entity.*;
import com.group4.model.*;
import com.group4.service.impl.ProductServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class SearchController {
    @Autowired
    ProductServiceImpl productServiceImpl = new ProductServiceImpl();
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String gpu,
            @RequestParam(required = false) String operationSystem,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String disk,
            @RequestParam(required = false) String category,
            Model model,
            HttpSession session) {
        session.removeAttribute("appliedPromotion");
        Page<ProductEntity> productEntities = productServiceImpl.searchProducts(
                searchName, manufacturer, cpu, gpu, operationSystem, minPrice, maxPrice, disk, category, PageRequest.of(page, 12));

//        List<ProductModel> products = productEntities.stream()
//                .map(this::convertToModel)
//                .collect(Collectors.toList());

        // Tính toán đánh giá trung bình và số lượng review cho từng sản phẩm
        List<ProductModel> products = productEntities.stream()
                .map(product -> {
                    ProductModel productModel = new ProductModel(product);
                    productModel.setAverageRating(productServiceImpl.calculateAverageRating(product));
                    productModel.setReviewCount(productServiceImpl.getReviewCount(product));
                    return productModel;
                })
                .toList();

        model.addAttribute("products", products);
        model.addAttribute("page", productEntities);

        // Gửi danh sách category để hiển thị trên giao diện
        List<CategoryModel> categories = productServiceImpl.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop-grid-left-sidebar";
    }
}
