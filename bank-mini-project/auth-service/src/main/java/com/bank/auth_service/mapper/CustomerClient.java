package com.bank.auth_service.mapper;

import com.bank.bank_common.dto.customer.request.CustomerCreateInternalRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "customer-service")
public interface CustomerClient {

    @PostMapping("/internal/customers")
    void createCustomer(@RequestBody CustomerCreateInternalRequest request);
}
