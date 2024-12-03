package com.group4.service.impl;

import com.group4.entity.UserEntity;
import com.group4.repository.UserRepository;
import com.group4.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void recoverPassword(String password, String email) {
        UserEntity user = userRepository.findByEmail(email).get();
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        return userRepository.existsByEmailAndPassword(username, password);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void updateActiveStatus(Long userId, boolean status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        user.setActive(status);
        userRepository.save(user);
    }
}
