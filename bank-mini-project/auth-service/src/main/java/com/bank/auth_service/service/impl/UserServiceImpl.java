package com.bank.auth_service.service.impl;


import com.bank.auth_service.entity.User;
import com.bank.auth_service.mapper.UserMapper;
import com.bank.auth_service.repository.UserRepository;
import com.bank.auth_service.service.UserService;
import com.bank.bank_common.dto.auth.response.EmailResponse;
import com.bank.bank_common.dto.auth.response.UserInfoResponse;
import com.bank.bank_common.dto.user.response.UserResponse;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponse getByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public EmailResponse getEmailById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new EmailResponse(user.getEmail()))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public String getPhone(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPhone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserInfoResponse getUserInfo(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
