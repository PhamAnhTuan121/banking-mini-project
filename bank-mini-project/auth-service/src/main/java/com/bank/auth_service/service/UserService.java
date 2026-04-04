package com.bank.auth_service.service;

import com.bank.bank_common.dto.auth.response.EmailResponse;

public interface UserService {
    EmailResponse getEmailById(Long id);
}
