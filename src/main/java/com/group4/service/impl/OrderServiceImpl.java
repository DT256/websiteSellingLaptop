package com.group4.service.impl;

import com.group4.dto.EmailDetail;
import com.group4.entity.*;
import com.group4.repository.CustomerRepository;
import com.group4.repository.OrderFailRepository;
import com.group4.repository.OrderRepository;
import com.group4.repository.ProductRepository;
import com.group4.service.IEmailService;
import com.group4.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderFailRepository orderFailRepository;
    @Autowired
    private IEmailService emailService;

    @Override
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
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

    @Override
    public List<OrderEntity> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<OrderEntity> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(Long userID) {
        return orderRepository.findOrdersByUserID(userID);
    }

    @Override
    public OrderEntity getOrderDetails(Long orderID) {
        return orderRepository.findById(orderID).get();
    }

    @Override
    public OrderEntity placeOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public void confirmCancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Thay đổi trạng thái đơn hàng thành "Đã hủy"
        order.setShippingStatus("Đã hủy");
        orderRepository.save(order);
    }

    @Override
    public void rejectCancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Thay đổi trạng thái đơn hàng thành "Đang giao" hoặc trạng thái trước đó
        order.setShippingStatus("Đang giao");
        orderRepository.save(order);
    }

    @Override
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

    @Override
    public void cancelOrder(Long orderId, String accountNumber, String accountName, String bankName) {
        // Lấy thông tin đơn hàng từ database
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra trạng thái giao hàng (chỉ cho phép hủy nếu chưa vận chuyển)
        if ("SHIPPED".equals(order.getShippingStatus())) {
            throw new IllegalStateException("Đơn hàng đã được vận chuyển, không thể hủy.");
        }

        // Tạo đối tượng OrderFailEntity để lưu thông tin hủy
        OrderFailEntity orderFail = new OrderFailEntity();
        orderFail.setOrderId(order.getOrderId());
        orderFail.setCustomer(order.getCustomer());
        orderFail.setShippingAddress(order.getShippingAddress());
        orderFail.setOrderDate(order.getOrderDate());
        orderFail.setReceiveDate(order.getReceiveDate());
        orderFail.setShippingStatus("CANCELLED"); // Đánh dấu trạng thái hủy
        orderFail.setShippingMethod(order.getShippingMethod());
        orderFail.setPhoneNumber(order.getPhoneNumber());
        orderFail.setNote(order.getNote());
        orderFail.setPaymentStatus(order.getPaymentStatus());
        orderFail.setListLineItems(order.getListLineItems());
        orderFail.setPayment(order.getPayment());
        orderFail.setBankName(bankName);
        orderFail.setAccountNumber(accountNumber);
        orderFail.setAccountName(accountName);

        // Lưu thông tin hủy vào bảng `orders-fail`
        orderFailRepository.save(orderFail);

        order.setShippingStatus("CANCELLED");
        orderRepository.save(order);

        EmailDetail emailDetail = new EmailDetail();

        String body = "Chào " + order.getCustomer().getName() + ",\n\n" +
                "Chúng tôi thông báo rằng đơn hàng của bạn (Mã đơn hàng: " + orderId +
                ") đã hủy.\n\n" +
                "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.\n\n" +
                "Trân trọng,\n";
        emailDetail.setMsgBody(body);
        emailDetail.setRecipient(order.getCustomer().getEmail());
        emailDetail.setSubject("Thông báo hủy đơn hàng");
        emailService.sendEmailConfirmCancelOrder(emailDetail);
    }
}