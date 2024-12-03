package com.group4.restController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.config.ResponseObject;
import com.group4.dto.PaymentDTO;
import com.group4.dto.QRPaymentRequest;
import com.group4.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private final IPaymentService paymentService;
    //Tạo mã QR thanh toán.
    @PostMapping("/qr")
    public ResponseEntity<Map<String, Object>> generateQRPayment(@RequestBody QRPaymentRequest request) {
        String qrCode = paymentService.generateQr(request.getOrderId(), request.getAmount());
        return ResponseEntity.ok(Map.of("qrCode", qrCode));
    }

    @PostMapping("qr-callback")
    public void qrPayCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        Long orderId = Long.parseLong(request.getParameter("orderId"));
        int requi_amount = Integer.parseInt(request.getParameter("requi_amount"));
        int paid_amount = Integer.parseInt(request.getParameter("paid_amount"));
        System.out.println(orderId + " " + requi_amount + " " + paid_amount);
        // Kiểm tra logic thanh toán
        if (paid_amount - requi_amount >= 0) {
            paymentService.handlePayQr(orderId,paid_amount);
            response.getWriter().write(
                    "<script>" +
                            "alert('Đơn hàng đã thanh toán thành công');" +
                            "window.location.href = '/history';"+
                            "</script>"
            );
            //return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            response.getWriter().write(
                    "<script>" +
                            "alert('Đơn hàng đã thanh toán không thành công');" +
                            "window.location.href = '/purchase/payment?orderId="+orderId +"&amount=" + requi_amount +
                            "</script>"
            );
            //return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", "Số tiền thanh toán không đủ"));
        }
    }

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO> bankPay(HttpServletRequest request) {
        return new ResponseObject<  >(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }
    @GetMapping("/vn-pay-callback")
    public void bankPayCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        String status = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String bankCode = request.getParameter("vnp_BankCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        long amount = Long.parseLong(request.getParameter("vnp_Amount"))/100;

        String payDate = request.getParameter("vnp_PayDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(payDate, formatter);

        Long orderId = Long.parseLong(request.getParameter("orderid"));

        if (status.equals("00")) {
            paymentService.handlePayBank(transactionNo,bankCode,transactionStatus,localDateTime,(int)amount, orderId);
            response.getWriter().write(
                    "<script>" +
                            "alert('Đơn hàng đã thanh toán thành công');" +
                            "window.location.href = '/history';"+
                            "</script>"
            );
            //return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            response.getWriter().write(
                    "<script>" +
                         "alert('Thanh toán thất bại. Vui lòng thử lại!');" + // Hiển thị popup lỗi//
                            "window.location.href = '/purchase/payment?orderId="+orderId +"&amount=" + amount +
                    "</script>"
            );
            //return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", "Số tiền thanh toán không đủ"));
        }
    }
}

