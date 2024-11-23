package com.group4.services;

import com.group4.entity.CategoryEntity;
import com.group4.entity.ProductEntity;
import com.group4.model.CategoryModel;
import com.group4.model.ProductModel;
import com.group4.repository.CategoryRepository;
import com.group4.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductEntity> searchProducts(String keyword, Double minPrice, Double maxPrice,
                                              String ram, String cpu, String gpu,
                                              String monitor, String disk, String manufacturerName) {
        if (keyword != null || !keyword.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword);
        }
        return productRepository.findByMultipleFilters(minPrice, maxPrice, ram, cpu, gpu, monitor, disk, manufacturerName);
    }

    public Page<ProductEntity> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<ProductEntity> searchProducts(String searchName, String manufacturer, String cpu, String gpu,
                                              String operationSystem, Integer minPrice, Integer maxPrice, String disk, String category, Pageable pageable) {
        // Gọi đến repository để lấy danh sách sản phẩm dựa trên các tiêu chí tìm kiếm
        return productRepository.findProductsByCriteria(searchName, manufacturer, cpu, gpu, operationSystem, minPrice, maxPrice, disk, category, pageable);
    }

    public List<CategoryModel> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        return categoryEntities.stream()
                .map(categoryEntity -> {
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setCategoryID(categoryEntity.getCategoryID());
                    categoryModel.setName(categoryEntity.getName());
                    categoryModel.setDescription(categoryEntity.getDescription());
                    return categoryModel;
                })
                .collect(Collectors.toList());
    }


}