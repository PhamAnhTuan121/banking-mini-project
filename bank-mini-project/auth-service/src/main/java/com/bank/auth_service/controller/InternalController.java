package com.bank.auth_service.controller;

import com.bank.auth_service.service.UserService;
import com.bank.bank_common.dto.auth.response.EmailResponse;

import com.bank.bank_common.dto.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/users")
@PreAuthorize("hasRole('INTERNAL')")
public class InternalController {

    private final UserService userService;


    @GetMapping("/{userId}")
    public UserResponse getByUserId(@PathVariable Long userId) {
       return userService.getByUserId(userId);
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<EmailResponse> getEmailUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getEmailById(id));
    }


    @GetMapping("/get/{userId}/phone")
    public String getPhone(@PathVariable Long userId) {
        return userService.getPhone(userId);
    }

}
