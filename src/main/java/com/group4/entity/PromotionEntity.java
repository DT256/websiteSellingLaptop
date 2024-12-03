package com.group4.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotions")
public class PromotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionID;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "remaining_uses", nullable = false)
    private int remainingUses;

    @Column(name = "valid_from", nullable = false)
    private Date validFrom;

    @Column(name = "valid_to", nullable = false)
    private Date validTo;

    @Column(name = "promotion_code", columnDefinition = "nvarchar(50)", unique = true)
    @NotBlank(message = "Promotion code is required")
    @Size(max = 50, message = "Promotion code cannot exceed 50 characters")
    private String promotionCode;

    @Column(columnDefinition = "nvarchar(1000)")
    private String description;
}
