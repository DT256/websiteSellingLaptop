package com.group4.service;

import com.group4.entity.*;
import com.group4.repository.CustomerRepository;
import com.group4.repository.OrderRepository;
import com.group4.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> searchOrders(String keyword, String status) {
        // Nếu cả keyword và status đều không được cung cấp, trả về tất cả đơn hàng
        if ((keyword == null || keyword.isEmpty()) && (status == null || status.isEmpty())) {
            return orderRepository.findAll();
        }

        //tìm bằng keyword và status
        if ((keyword != null && !keyword.isEmpty()) && (status != null && !status.isEmpty())) {
            return orderRepository.findByOrderIdAndStatus(keyword, status);

        }

        // Nếu chỉ có status, tìm theo trạng thái
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByStatus(status);
        }

        // Nếu chỉ có keyword, tìm theo mã đơn hàng
        if (keyword != null && !keyword.isEmpty()) {
            return orderRepository.findByOrderId(keyword);
        }
        return null;

    }
    public List<OrderEntity> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Optional<OrderEntity> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    public List<OrderEntity> getOrdersByUserId(Long userID) {
        return orderRepository.findOrdersByUserID(userID);
    }

    public OrderEntity getOrderDetails(Long orderID) {
        return orderRepository.findById(orderID).get();
    }

    public OrderEntity placeOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity createOrder(Long userId, List<Long> productIds) {

        Optional<CustomerEntity> user = customerRepository.findCustomerById(userId);
        // Tạo đơn hàng mới
        OrderEntity order = new OrderEntity();
        order.setCustomer(user.get());
        order.setPaymentStatus("Pending");
        order.setShippingStatus("Pending");
        order.setOrderDate(LocalDateTime.now());
        order.setReceiveDate(LocalDateTime.now().plusDays(7)); // Tạm thời set nhận hàng sau 7 ngày
        order.setPhoneNumber(user.get().getPhone());

        // Xử lý sản phẩm trong đơn hàng
        int total = 0;
        List<LineItemEntity> lineItems = new ArrayList<>();
        for (Long productId : productIds) {
            Optional<ProductEntity> productOtn = productRepository.findById(productId);
            ProductEntity product =productOtn.get();

            LineItemEntity lineItem = new LineItemEntity();
            lineItem.setOrder(order);
            lineItem.setProduct(product);
            lineItem.setQuantity(1); // Mặc định số lượng là 1
            lineItems.add(lineItem);

            total += product.getPrice();
        }
        order.setListLineItems(lineItems);

        // Lưu đơn hàng vào DB
        return orderRepository.save(order);
    }
}
