package com.bank.customer_service.service;

import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse getByUserId(Long userId);

    CustomerResponse updateProfile(Long userId, CustomerUpdateRequest request);

    void verifyPhoneOtp(Long userId , String otp);

    void requestPhoneOtp(Long userId, String phone);
}
