package com.bank.customer_service.mapper;

import com.bank.bank_common.dto.customer.request.CustomerCreateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.customer_service.entity.Customer;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    // Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kycStatus", constant = "PENDING")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "dailyLimit", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "usedToday", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "lastResetDate", expression = "java(java.time.LocalDate.now())")
    Customer toEntity(CustomerCreateRequest request);

    CustomerResponse toResponse(Customer customer);

}
