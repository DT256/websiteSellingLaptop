package com.group4.controller;


import com.group4.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;



    // Thống kê số lượng sản phẩm theo tên sản phẩm
    @GetMapping("/stats-by-name")
    public ResponseEntity<List<Map<String, Object>>> statsByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Map<String, Object>> stats = productService.countProductsByName(page, size);
        return ResponseEntity.ok(stats);
    }

    // Thống kê số lượng sản phẩm theo tên thể loại với phân trang
    @GetMapping("/stats-by-category")
    public ResponseEntity<List<Map<String, Object>>> statsByCategoryName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Map<String, Object>> stats = productService.countProductsByCategoryName(page, size);
        return ResponseEntity.ok(stats);
    }

    // Thống kê số lượng sản phẩm theo tên nhà sản xuất với phân trang
    @GetMapping("/stats-by-manufacturer")
    public ResponseEntity<List<Map<String, Object>>> statsByManufacturerName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Map<String, Object>> stats = productService.countProductsByManufacturerName(page, size);
        return ResponseEntity.ok(stats);
    }
}
