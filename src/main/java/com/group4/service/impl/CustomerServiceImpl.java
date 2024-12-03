package com.group4.service.impl;

import com.group4.entity.CustomerEntity;
import com.group4.entity.UserEntity;
import com.group4.repository.CustomerRepository;
import com.group4.repository.UserRepository;
import com.group4.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CustomerEntity> searchCustomers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return customerRepository.findAllCustomers();
        }
        return customerRepository.findByNameOrEmail(keyword, keyword);
    }

    // Lấy danh sách khách hàng
    @Override
    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAllCustomers();
    }

    // Phương thức lấy thông tin khách hàng theo ID
    @Override
    public Optional<CustomerEntity> getCustomerById(Long id) {
        return customerRepository.findCustomerById(id);
    }

    // Cập nhật trạng thái hoạt động
    @Override
    public void updateActiveStatus(Long userId, boolean status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + userId));
        user.setActive(status);
        userRepository.save(user); // Lưu thay đổi
    }
}
