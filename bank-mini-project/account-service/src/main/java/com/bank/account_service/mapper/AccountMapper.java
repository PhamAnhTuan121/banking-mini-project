package com.bank.account_service.mapper;

import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);
}
