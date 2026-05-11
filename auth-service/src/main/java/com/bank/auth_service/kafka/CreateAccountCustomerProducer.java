package com.bank.auth_service.kafka;

import com.bank.bank_common.dto.event.UserActivatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateAccountCustomerProducer {

    private final KafkaTemplate<String , UserActivatedEvent> kafkaTemplate;

    public void send(UserActivatedEvent event) {
        kafkaTemplate.send("user-activated-topic", event);
    }


}
