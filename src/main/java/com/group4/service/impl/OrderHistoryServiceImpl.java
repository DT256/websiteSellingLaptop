package com.group4.service.impl;

import com.group4.entity.AddressEntity;
import com.group4.entity.LineItemEntity;
import com.group4.entity.OrderEntity;
import com.group4.entity.UserEntity;
import com.group4.model.*;
import com.group4.repository.OrderHistoryRepository;
import com.group4.service.IOrderHistoryService;
import com.group4.service.IPersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderHistoryServiceImpl implements IOrderHistoryService {

    @Autowired
    private IPersonalInfoService personalInfoService;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    private OrderModel convertToModel(OrderEntity orderEntity) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderEntity.getOrderId());

        // Chuyển đổi user thông qua OrderEntity
        orderModel.setUser(convertToUserModel(orderEntity.getCustomer().getUserID()));

        // Chuyển đổi shippingAddress
        orderModel.setShippingAddress(convertToAddressModel(orderEntity.getShippingAddress()));

        // Chuyển đổi ngày và trạng thái
        orderModel.setOrderDate(orderEntity.getOrderDate());
        orderModel.setReceiveDate(orderEntity.getReceiveDate());
        orderModel.setShippingStatus(orderEntity.getShippingStatus());
        orderModel.setShippingMethod(orderEntity.getShippingMethod());
        orderModel.setPhoneNumber(orderEntity.getPhoneNumber());
        orderModel.setNote(orderEntity.getNote());
        orderModel.setPaymentStatus(orderEntity.getPaymentStatus());
        orderModel.setListLineItems(orderEntity.getListLineItems().stream().map(this::convertToLineItemModel)  // Ánh xạ OrderEntity thành OrderModel
                .collect(Collectors.toList()));
        return orderModel;
    }

    @Override
    public List<OrderModel> getPurchasedProducts(Long userId) {
        // Lấy danh sách OrderEntity từ repository
        List<OrderEntity> orderEntities = orderHistoryRepository.findPurchasedProductsByUserId(userId);

        // Chuyển đổi OrderEntity sang OrderModel
        return orderEntities.stream()
                .map(this::convertToModel)  // Ánh xạ OrderEntity thành OrderModel
                .collect(Collectors.toList());
    }

    private AddressModel convertToAddressModel(AddressEntity addressEntity) {
        AddressModel addressModel = new AddressModel();
        addressModel.setAddressID(addressEntity.getAddressID());
        addressModel.setCountry(addressEntity.getCountry());
        addressModel.setProvince(addressEntity.getProvince());
        addressModel.setDistrict(addressEntity.getDistrict());
        addressModel.setCommune(addressEntity.getCommune());
        addressModel.setOther(addressEntity.getOther());
        return addressModel;
    }

    private LineItemModel convertToLineItemModel(LineItemEntity lineItemEntity) {
        LineItemModel lineItemModel = new LineItemModel();
        lineItemModel.setId(lineItemEntity.getId());

        if (lineItemEntity.getProduct() != null) {
            lineItemModel.setProduct(new ProductModel(lineItemEntity.getProduct().getProductID(), lineItemEntity.getProduct().getName(), lineItemEntity.getProduct().getPrice(), lineItemEntity.getProduct().getStatus()));
        }

        lineItemModel.setQuantity(lineItemEntity.getQuantity());
        lineItemModel.setTotal(lineItemEntity.getProduct().getPrice()); // Assuming product has price
        lineItemModel.setProduct(new ProductModel(lineItemEntity.getProduct().getProductID(),
                lineItemEntity.getProduct().getName(),
                lineItemEntity.getProduct().getPrice(),
                lineItemEntity.getProduct().getStatus()
                ));
        return lineItemModel;
    }

    private UserModel convertToUserModel(Long userID) {
        Optional<UserEntity> userEntity = personalInfoService.findUserById(userID); // Hoặc gọi repository
        return new UserModel(userEntity.get().getUserID(), userEntity.get().getName(), userEntity.get().getEmail(),
                userEntity.get().getPassword(), userEntity.get().getGender(), userEntity.get().getPhone(),
                userEntity.get().getRoleName(), userEntity.get().isActive(), convertToAddressModel(userEntity.get().getAddress()));
    }
}