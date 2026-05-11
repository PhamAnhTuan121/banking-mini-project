package com.bank.transaction_service.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Long principal = Long.parseLong(auth.getName());
        return principal;
    }
}
