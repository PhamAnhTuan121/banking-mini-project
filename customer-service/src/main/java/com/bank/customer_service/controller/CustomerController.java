package com.bank.customer_service.controller;

import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.bank_common.dto.customer.response.UserProfileResponse;
import com.bank.customer_service.service.CustomerService;
import com.bank.customer_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    public UserProfileResponse me() {
        Long userId = SecurityUtils.getCurrentUserId();
        return customerService.getByUserId(userId);
    }

    @PutMapping("/me")
    public CustomerResponse updateProfile(
            @RequestBody CustomerUpdateRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        return customerService.updateProfile(userId, request);
    }

}