package com.bank.account_service.kafka;

import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.event.StatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StatusConsumer {
    
    private final AccountService accountService;

    @KafkaListener(topics = "status-events" , groupId = "account-group-2")
    public void handleUserEvent(StatusEvent event) {
        if("USER_BLOCKED".equals(event.getEventType())) {
            accountService.blockAccount(event.getUserId());

        }

        if("USER_UNBLOCKED".equals(event.getEventType())) {
            accountService.unblockAccount(event.getUserId());
        }
    }

}
