package com.group4.service;

import com.group4.entity.CustomerEntity;
import com.group4.entity.UserEntity;
import com.group4.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    public List<CustomerEntity> searchCustomers(String keyword);
    public List<CustomerEntity> getAllCustomers();
    public Optional<CustomerEntity> getCustomerById(Long id);
    public void updateActiveStatus(Long userId, boolean status);
}
