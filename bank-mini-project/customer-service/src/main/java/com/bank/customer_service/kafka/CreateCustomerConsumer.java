package com.bank.customer_service.kafka;

import com.bank.bank_common.dto.event.UserActivatedEvent;
import com.bank.customer_service.service.InternalCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCustomerConsumer {

    private final InternalCustomerService internalCustomerService;

    @KafkaListener(topics = "user-activated-topic", groupId = "customer-group")
    public void handleUserActivated(UserActivatedEvent event) {

        System.out.println("🔥 EVENT RECEIVED FROM KAFKA");

        System.out.println(event);

        internalCustomerService.createDefaultCustomer(
                event.getUserId(),
                event.getFullName(),
                event.getPhone()
        );
    }

}
