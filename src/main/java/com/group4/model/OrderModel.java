package com.group4.model;

import com.group4.entity.PaymentEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderModel {
    private Long orderId;
    private UserModel user;
    private AddressModel shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime  receiveDate;
    //Trạng thái giao hàng
    private String shippingStatus;
    //Phương thức thanh toán
    private PaymentModel payment;
    // Phương thức giao hàng
    private String shippingMethod;
    private String phoneNumber;
    private String note;
    private int totalPrice;
    private List<LineItemModel> listLineItems;

    public int getTotalAmount(){
        int totalAmount = 0;
        for(LineItemModel lineItemModel : listLineItems){
            totalAmount = lineItemModel.getTotalAmount();
        }
        return totalAmount;
    }
}

