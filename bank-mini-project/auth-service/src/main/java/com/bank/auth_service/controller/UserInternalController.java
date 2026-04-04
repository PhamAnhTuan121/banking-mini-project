package com.bank.auth_service.controller;

import com.bank.auth_service.service.UserService;
import com.bank.bank_common.dto.auth.response.EmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/users")
public class UserInternalController {

    private final UserService userService;

    @GetMapping("/{id}/email")
    public ResponseEntity<EmailResponse> getEmailUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getEmailById(id));
    }
}
