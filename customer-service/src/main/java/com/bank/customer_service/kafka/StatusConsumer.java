package com.bank.customer_service.kafka;

import com.bank.bank_common.dto.event.StatusEvent;
import com.bank.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusConsumer {

    private final CustomerService customerService;

    @KafkaListener(topics = "status-events" , groupId = "customer-group")
    public void handleUserEvent(StatusEvent event) {
        if ("USER_BLOCKED".equals(event.getEventType())) {
            customerService.blockUser(event.getUserId());
        } else if ("USER_UNBLOCKED".equals(event.getEventType())) {
            customerService.unblock(event.getUserId());
        }
    }
}
