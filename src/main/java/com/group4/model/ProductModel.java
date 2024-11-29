package com.group4.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Long productID;
    private String name;
    private int price;
    private int status;
    private CategoryModel category;
    private ManufacturerModel manufacturer;
    private ProductDetailModel detail;

    public ProductModel(Long productID, String name, int price, int status) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.status = status;
    }
}
