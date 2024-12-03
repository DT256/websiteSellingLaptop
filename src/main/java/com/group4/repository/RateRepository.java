package com.group4.repository;

import com.group4.entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {

    // Kiểm tra xem đánh giá đã tồn tại dựa trên userId và productId
    boolean existsByUser_UserIDAndProduct_ProductID(Long userId, Long productId);

    @Query("SELECT AVG(r.rate) FROM RateEntity r WHERE r.product.productID = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM RateEntity r WHERE r.product.productID = :productId")
    Integer findReviewCountByProductId(@Param("productId") Long productId);
}

