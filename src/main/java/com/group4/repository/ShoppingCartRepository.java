package com.group4.repository;

import com.group4.entity.ProductEntity;
import com.group4.entity.ShoppingCartEntity;
import com.group4.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity,Long> {
    Optional<ShoppingCartEntity> findByCustomer(UserEntity userEntity);
    @Query("SELECT p FROM ShoppingCartEntity sc JOIN sc.products p WHERE sc.customer.userID = :customerID")
    List<ProductEntity> findProductsByCustomerId(@Param("customerID") Long customerId);
}
