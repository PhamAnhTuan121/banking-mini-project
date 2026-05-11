package com.bank.auth_service.repository;

import com.bank.auth_service.entity.RefreshToken;
import com.bank.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
