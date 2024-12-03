package com.group4.service;

import com.group4.entity.AddressEntity;
import com.group4.entity.CustomerEntity;
import com.group4.entity.LineItemEntity;
import com.group4.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
    public List<OrderEntity> getAllOrders();
    public List<OrderEntity> searchOrders(String keyword, String status);
    public List<OrderEntity> getOrdersByCustomerId(Long customerId);
    public Optional<OrderEntity> getOrderById(Long orderId);
    public List<OrderEntity> getOrdersByUserId(Long userID);
    public OrderEntity getOrderDetails(Long orderID);
    public OrderEntity placeOrder(OrderEntity order);
    public OrderEntity createOrder(List<LineItemEntity>lineItems, CustomerEntity currentUser, AddressEntity address,long total, boolean checkAddress, String phone, String note);
    public int getTotalOrderValue(OrderEntity order);
    public int checkInventoryProduct(List<LineItemEntity> lineItems);
    public int checkStatusProduct(List<LineItemEntity> lineItems);
    public OrderEntity createOrder(Long userId, List<Long> productIds);
}
