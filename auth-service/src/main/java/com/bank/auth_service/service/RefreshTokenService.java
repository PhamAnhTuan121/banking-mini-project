package com.bank.auth_service.service;

import com.bank.auth_service.entity.RefreshToken;
import com.bank.auth_service.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserId(Long userId);
}
