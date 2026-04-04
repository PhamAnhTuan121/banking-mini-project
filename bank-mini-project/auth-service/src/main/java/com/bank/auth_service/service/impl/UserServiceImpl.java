package com.bank.auth_service.service.impl;

import com.bank.auth_service.repository.UserRepository;
import com.bank.auth_service.service.UserService;
import com.bank.bank_common.dto.auth.response.EmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public EmailResponse getEmailById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new EmailResponse(user.getEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
