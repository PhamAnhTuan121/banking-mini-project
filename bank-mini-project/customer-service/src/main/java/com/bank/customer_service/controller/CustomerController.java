package com.bank.customer_service.controller;

import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.request.PhoneRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.bank_common.dto.event.UserActivatedEvent;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.customer_service.service.CustomerService;
import com.bank.customer_service.service.InternalCustomerService;
import com.bank.customer_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final InternalCustomerService internalCustomerService;

    @GetMapping("/me")
    public CustomerResponse me() {
        Long userId = SecurityUtils.getCurrentUserId();
        return customerService.getByUserId(userId);
    }

    @PutMapping("/me")
    public CustomerResponse updateProfile(
            @RequestBody CustomerUpdateRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        return customerService.updateProfile(userId, request);
    }

    @PostMapping("/phone/request")
    public void requestPhoneOtp(
            @RequestBody PhoneRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        customerService.requestPhoneOtp(userId, request.getPhone());
    }

    @PostMapping("/phone/verify")
    public void verifyPhoneOtp(
            @RequestBody VerifyOtpRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        customerService.verifyPhoneOtp(userId, request.getOtp());
    }

}