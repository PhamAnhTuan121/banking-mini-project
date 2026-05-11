package com.bank.transaction_service.mapper;

import com.bank.transaction_service.dto.transaction.response.TransactionResponse;
import com.bank.transaction_service.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "transactionType")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "correlationId", source = "correlationId")
    @Mapping(target = "fromAccount", source = "fromAccount")
    @Mapping(target = "toAccount", source = "toAccount")
    @Mapping(target = "status", expression = "java(transaction.getStatus().name())")
    @Mapping(target = "timestamp", source = "createdAt")
    TransactionResponse toResponse(Transaction transaction);
}
