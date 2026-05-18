package com.bank.auth_service.service;

import com.bank.bank_common.dto.auth.response.EmailResponse;
import com.bank.bank_common.dto.auth.response.UserInfoResponse;
import com.bank.bank_common.dto.user.response.UserResponse;

public interface UserService {

    UserResponse getByUserId(Long userId);

    EmailResponse getEmailById(Long id);

    String getPhone(Long userId);

    UserInfoResponse getUserInfo(Long id);
}
