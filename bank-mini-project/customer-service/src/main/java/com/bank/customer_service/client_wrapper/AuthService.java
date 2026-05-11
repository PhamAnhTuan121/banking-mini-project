package com.bank.customer_service.client_wrapper;

import com.bank.bank_common.dto.customer.request.UpdatePhoneRequest;
import com.bank.customer_service.client.AuthClient;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    @Retry(name = "authService")
    public  void updatePhone(Long userId, String phone ) {
        try {
            authClient.updatePhone(userId, new UpdatePhoneRequest(phone) );
            System.out.println("UPDATE PHONE SUCCESS");
        } catch (Exception e) {
            System.out.println("UPDATE PHONE FAIL");
            e.printStackTrace();
            throw e;
        }
    }

    @Retry(name = "authService", fallbackMethod = "getPhoneFallback")
    public String getPhone(Long userId) {
        return authClient.getPhone(userId);
    }

    public String getPhoneFallback(Long userId, Exception e) {
        System.err.println("All retry attempts failed for user: " + userId);
        return "Phone unavailable";
    }
}
