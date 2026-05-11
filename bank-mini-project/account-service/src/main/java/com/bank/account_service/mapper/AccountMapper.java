package com.bank.account_service.mapper;


import com.bank.account_service.entity.Account;
import com.bank.bank_common.dto.account.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);
}
