package com.group4.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "imageItems")
public class ImageItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "name", columnDefinition = "nvarchar(250)", nullable = false)
    private String name;

    @Column(name = "image_url",columnDefinition = "text", nullable = false)
    private String imageUrl;

//    @ManyToOne
//    @JoinColumn(name = "product_detail_id", nullable = false)
//    private ProductDetailEntity productDetail;

    @ManyToOne
    @JoinColumn(name = "product_detail_id", nullable = false)
    @ToString.Exclude // Loại trừ tham chiếu vòng lặp
    @JsonManagedReference
    private ProductDetailEntity productDetail;

}