package com.bank.customer_service.service;

import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.bank_common.dto.customer.response.UserProfileResponse;

public interface CustomerService {

    UserProfileResponse getByUserId(Long userId);

    void blockUser(Long userId);
    void unblock(Long userId);

    CustomerResponse updateProfile(Long userId, CustomerUpdateRequest request);

    void requestOldPhoneOtp(Long userId);

    void verifyOldPhoneOtp(Long userId, String otp);

    void verifyPhoneOtp(Long userId , String otp);

    void requestPhoneOtp(Long userId, String phone);
}
