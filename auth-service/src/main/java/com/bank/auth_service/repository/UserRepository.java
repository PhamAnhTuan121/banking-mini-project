package com.bank.auth_service.repository;

import com.bank.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


}
