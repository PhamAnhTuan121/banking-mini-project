package com.bank.auth_service.service;

import com.bank.bank_common.dto.auth.response.EmailResponse;
import com.bank.bank_common.dto.user.response.UserResponse;

public interface UserService {

    UserResponse getByUserId(Long userId);

    EmailResponse getEmailById(Long id);

    void updatePhone(Long userId, String newPhone);

    String getPhone(Long userId);
}
