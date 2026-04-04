package com.bank.notification_service.mapper;

import com.bank.bank_common.dto.notification.request.NotificationRequest;
import com.bank.notification_service.dto.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "status", constant = "SENT")
    @Mapping(target = "detail", source = "message")
    NotificationResponse toResponse(NotificationRequest request);
}
