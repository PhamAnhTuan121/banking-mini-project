package com.bank.auth_service.service.impl;


import com.bank.auth_service.entity.User;
import com.bank.auth_service.kafka.StatusProducer;
import com.bank.auth_service.mapper.UserMapper;
import com.bank.auth_service.repository.UserRepository;
import com.bank.auth_service.service.UserService;
import com.bank.auth_service.util.AuditUtil;
import com.bank.bank_common.dto.auth.response.EmailResponse;

import com.bank.bank_common.dto.event.StatusEvent;
import com.bank.bank_common.dto.user.response.UserResponse;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final AuditUtil auditUtil;
    private final StatusProducer statusProducer;
    private final UserMapper userMapper;

    @Override
    public UserResponse getByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse) // Assuming userMapper is injected
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public EmailResponse getEmailById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new EmailResponse(user.getEmail()))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void updatePhone(Long userId, String newPhone) {

        if (userRepository.existsByPhone(newPhone)) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setPhone(newPhone);
        userRepository.save(user);

        // 🔥 publish event sync sang customer
        statusProducer.sendUserUpdated(new StatusEvent(
                UUID.randomUUID().toString(),
                userId,
                "USER_PHONE_UPDATED",
                LocalDateTime.now()
        ));

        auditUtil.success(user.getId(), user.getUsername(),
                "UPDATE_PHONE", "UPDATE", "Phone updated");
    }

    @Override
    public String getPhone(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPhone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
