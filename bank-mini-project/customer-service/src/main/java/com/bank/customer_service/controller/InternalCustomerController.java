package com.bank.customer_service.controller;

import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.customer_service.service.InternalCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/customers")
@RequiredArgsConstructor
public class InternalCustomerController {

    private final InternalCustomerService internalCustomerService;

    @GetMapping("/{userId}")
    public CustomerResponse getByUserId(@PathVariable Long userId) {
        return internalCustomerService.getByUserId(userId);
    }

}
