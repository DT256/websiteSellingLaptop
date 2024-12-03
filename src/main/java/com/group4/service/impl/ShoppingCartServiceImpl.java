package com.group4.service.impl;

import com.group4.entity.CustomerEntity;
import com.group4.entity.ProductEntity;
import com.group4.entity.ShoppingCartEntity;
import com.group4.entity.UserEntity;
import com.group4.repository.ProductRepository;
import com.group4.repository.ShoppingCartRepository;
import com.group4.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements IShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ShoppingCartEntity addProductToCart(CustomerEntity customer, ProductEntity product) {
        // Find the shopping cart by user
        Optional<ShoppingCartEntity> optionalCart = shoppingCartRepository.findCartByCustomer(customer);

        ShoppingCartEntity shoppingCart;
        if (optionalCart.isPresent()) {
            // Cart already exists, add the product
            shoppingCart = optionalCart.get();
        } else {
            // Create new cart for user
            shoppingCart = new ShoppingCartEntity();
            shoppingCart.setCustomer(customer);
        }
        List<ProductEntity> products = shoppingCart.getProducts();
        if (products == null) {
            products = new ArrayList<>();
        }
        // Check if the product already exists in the cart
        boolean isProductInCart = products.stream()
                .anyMatch(existingProduct -> existingProduct.getProductID().equals(product.getProductID()));

        if (!isProductInCart) {
            // Add the product only if it does not already exist
            products.add(product);
            shoppingCart.setProducts(products);
        }

        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public List<ProductEntity> findProductsByCustomerId(Long customerID) {
        return shoppingCartRepository.findProductsByCustomerId(customerID);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        // Lấy giỏ hàng của khách hàng
        ShoppingCartEntity cart = shoppingCartRepository.findShoppingCartEntityByCustomerUserID(customerId)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found for customer ID: " + customerId));

        // Tìm sản phẩm cần xóa
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Xóa sản phẩm khỏi danh sách
        cart.getProducts().remove(product);

        // Lưu lại giỏ hàng
        shoppingCartRepository.save(cart);
    }

}
