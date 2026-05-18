package com.bank.auth_service.controller;

import com.bank.auth_service.entity.User;
import com.bank.auth_service.repository.UserRepository;
import com.bank.auth_service.service.AuthService;
import com.bank.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userRepository.findAll()
        );
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        authService.blockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        authService.unblockUser(id);
        return ResponseEntity.ok().build();
    }
}