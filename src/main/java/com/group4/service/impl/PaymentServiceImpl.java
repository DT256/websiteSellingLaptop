package com.group4.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.config.VNPAYConfig;
import com.group4.config.VietQRConfig;
import com.group4.dto.EmailDetail;
import com.group4.dto.PaymentDTO;
import com.group4.entity.OrderEntity;
import com.group4.entity.PaymentEntity;
import com.group4.service.IPaymentService;
import com.group4.repository.OrderRepository;
import com.group4.repository.PaymentRepository;
import com.group4.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final VNPAYConfig vnPayConfig;
    private final VietQRConfig vietQRConfig;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final PaymentRepository paymentRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public String generateQr(int orderId, int amount) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> vietQRPrams = vietQRConfig.getVietQRConfig();
        String requestBody = "{"
                + "\"accountNo\": \"" + vietQRPrams.get("accountNumber") + "\", "
                + "\"accountName\": \"" + vietQRPrams.get("accountName") + "\", "
                + "\"acqId\": \"" + vietQRPrams.get("bankCode") + "\", "
                + "\"addInfo\": \"Thanh toán cho hóa đơn " + orderId + "\", "
                + "\"amount\": " + amount + ", "
                + "\"template\": \"qr_only\""
                + "}";
        System.out.println(requestBody);

        // Thiết lập Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", vietQRPrams.get("clientKey"));
        headers.set("x-api-key", vietQRPrams.get("apiKey"));
        headers.set("Content-Type", "application/json");

        // Gửi yêu cầu HTTP POST
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(vietQRPrams.get("payUrl"), HttpMethod.POST, entity, String.class);

            // Xử lý phản hồi từ API
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                String qrDataURL = jsonResponse.path("data").path("qrDataURL").asText();

                return qrDataURL;
            } else {
                throw new RuntimeException("Không thể tạo mã QR từ VietQR: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi API VietQR: " + e.getMessage(), e);
        }
    }
    @Override
    public void handlePayQr(Long orderId, int amount) {
        Optional<OrderEntity> orderOtn = orderRepository.findById(orderId);
        OrderEntity order = orderOtn.get();
        order.setPaymentStatus("Completed");

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setTransactionID(orderId.toString());
        paymentEntity.setPaymentMethod("QR");
        paymentEntity.setPaymentStatus("Completed");
        paymentEntity.setPaymentDate(LocalDateTime.now());
        paymentEntity.setTotal(amount);
        paymentEntity.setOrder(order);

        paymentRepository.save(paymentEntity);
        order.setPayment(paymentEntity);
        orderRepository.save(order);

        EmailDetail emailDetail = new EmailDetail();

        String body = "Chào " + order.getCustomer().getName() + ",\n\n" +
                "Chúng tôi xác nhận rằng bạn đã thanh toán thành công cho đơn hàng (Mã đơn hàng: " + orderId + ").\n" +
                "Số tiền thanh toán: " + amount + " VND.\n" +
                "Ngày thanh toán: " + LocalDateTime.now() + ".\n\n" +
                "Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng của chúng tôi.\n\n" +
                "Trân trọng,\nYour Company Name";
        emailDetail.setMsgBody(body);
        emailDetail.setRecipient(order.getCustomer().getEmail());
        emailDetail.setSubject("Thông báo thánh toán đơn hàng");
        emailService.sendInvoice(emailDetail);
    }

    @Override
    public PaymentDTO createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String orderId = request.getParameter("orderId");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    @Override
    public void handlePayBank(String transactionNo, String bankCode, String transactionStatus, LocalDateTime localDateTime, int amount, Long orderId){
        Optional<OrderEntity> orderOtn = orderRepository.findById(orderId);
        OrderEntity order = orderOtn.get();
        order.setPaymentStatus("Completed");

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setTransactionID(transactionNo);
        paymentEntity.setPaymentMethod(bankCode);
        paymentEntity.setPaymentStatus(transactionStatus);
        paymentEntity.setPaymentDate(localDateTime);
        paymentEntity.setTotal(amount);
        paymentEntity.setOrder(order);

        paymentRepository.save(paymentEntity);
        order.setPayment(paymentEntity);
        orderRepository.save(order);

        EmailDetail emailDetail = new EmailDetail();

        String body = "Chào " + order.getCustomer().getName() + ",\n\n" +
                "Chúng tôi xác nhận rằng bạn đã thanh toán thành công cho đơn hàng (Mã đơn hàng: " + orderId + ").\n" +
                "Số tiền thanh toán: " + amount + " VND.\n" +
                "Ngày thanh toán: " + localDateTime + ".\n\n" +
                "Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng của chúng tôi.\n\n" +
                "Trân trọng,\nYour Company Name";
        emailDetail.setMsgBody(body);
        emailDetail.setRecipient(order.getCustomer().getEmail());
        emailDetail.setSubject("Thông báo thánh toán đơn hàng");
        emailService.sendInvoice(emailDetail);
    }



    public Double getDailyRevenue(LocalDate date) {
        return paymentRepository.getRevenueByDay(date);
    }

    public List<Map<String, Object>> getYearlyRevenue(int year) {
        return paymentRepository.getMonthlyRevenue(year);
    }
}
