package com.bank.notification_service.client_wrapper;

import com.bank.bank_common.dto.auth.response.UserInfoResponse;
import com.bank.notification_service.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    public UserInfoResponse getUserById(Long userId) {
        return userClient.getUserById(userId);
    }

}