package com.group4.service;

import com.group4.entity.ProductEntity;
import com.group4.model.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProductService {
    public List<ProductEntity> searchProducts(String keyword, Double minPrice, Double maxPrice,
                                              String ram, String cpu, String gpu,
                                              String monitor, String disk, String manufacturerName);

    public Page<ProductEntity> findAll(Pageable pageable);

    public Page<ProductEntity> searchProducts(String searchName, String manufacturer, String cpu, String gpu,
                                              String operationSystem, Integer minPrice, Integer maxPrice, String disk, String category, Pageable pageable);

    public List<CategoryModel> getAllCategories();

    List<ProductEntity> findAll();

    Optional<ProductEntity> findById(Long id);

    ProductEntity save(ProductEntity productEntity);
    
    public List<ProductEntity> findByName(String producName);
    
    public Page<ProductEntity> findAllDistinctByName(Pageable pageable);
    
    void deleteById(Long id);

    public double calculateAverageRating(ProductEntity product);

    public int getReviewCount(ProductEntity product);

    public Page<ProductEntity> getProducts(int page, int size);

}
