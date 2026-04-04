package com.bank.customer_service.service;

import com.bank.bank_common.dto.customer.response.CustomerResponse;

public interface InternalCustomerService {
    void createDefaultCustomer(Long userId, String fullName, String phoneNumber);

    CustomerResponse getByUserId(Long userId);

    void blockCustomer(Long userId);
}
