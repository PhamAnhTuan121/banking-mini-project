package com.bank.customer_service.kafka;

import com.bank.bank_common.dto.event.UserActivatedEvent;
import com.bank.bank_common.exception.BusinessException;
import com.bank.customer_service.service.InternalCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCustomerConsumer {

    private final InternalCustomerService internalCustomerService;

    @KafkaListener(topics = "user-activated-topic", groupId = "customer-group")
    public void handleUserActivated(UserActivatedEvent event) {

        try {
            internalCustomerService.createDefaultCustomer(
                    event.getUserId()
            );

        } catch (BusinessException e) {
            log.warn("Business error, skip event: {}", e.getMessage());
        }
    }
}