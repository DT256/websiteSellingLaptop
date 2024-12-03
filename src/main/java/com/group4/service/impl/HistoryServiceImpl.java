package com.group4.service.impl;

import com.group4.dto.EmailDetail;
import com.group4.entity.OrderEntity;
import com.group4.entity.OrderFailEntity;
import com.group4.repository.HistoryRepository;
import com.group4.repository.OrderFailRepository;
import com.group4.repository.OrderRepository;
import com.group4.service.IEmailService;
import com.group4.service.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryServiceImpl implements IHistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFailRepository orderFailRepository;

    @Autowired
    private IEmailService emailService;

    @Override
    public List<OrderEntity> getPurchaseHistory(Long userID) {
        return historyRepository.getHistory(userID);
    }

    @Override
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public void cancelOrder(Long orderId, String accountNumber, String accountName, String bankName) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

            OrderFailEntity orderFail = new OrderFailEntity();
            orderFail.setOrderId(orderId);
            orderFail.setAccountName(accountName);
            orderFail.setBankName(bankName);
            orderFail.setAccountNumber(accountNumber);

            orderFailRepository.save(orderFail);

            System.out.println(order);

            order.setShippingStatus("CANCEL");
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
