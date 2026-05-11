package com.bank.account_service.kafka;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.event.UserActivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(
            topics = "user-activated-topic",
            groupId = "account-group"
    )
    public void consume(UserActivatedEvent event) {

        log.info("Received event: {}", event);

        try {

            accountService.createAccount(
                    new AccountCreateRequest(
                            event.getUserId()
                    )
            );

            log.info("Account created for userId={}",
                    event.getUserId());

        } catch (Exception e) {

            log.error("Create account failed", e);
        }
    }
}