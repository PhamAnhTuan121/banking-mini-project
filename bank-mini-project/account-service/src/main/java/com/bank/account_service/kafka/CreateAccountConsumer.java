package com.bank.account_service.kafka;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.event.UserActivatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = "user-activated-topic" , groupId = "account-group")
    public void send(UserActivatedEvent event){
        try {
            accountService.createAccount(new AccountCreateRequest(event.getUserId()));
        } catch (Exception e) {
            System.err.println("Account creation error (may exist): " + e.getMessage());
        }
    }
}
