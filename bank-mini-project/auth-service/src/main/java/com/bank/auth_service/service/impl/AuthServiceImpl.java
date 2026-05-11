package com.bank.auth_service.service.impl;


import com.bank.auth_service.dto.request.*;
import com.bank.auth_service.dto.response.AuthResponse;
import com.bank.auth_service.entity.RefreshToken;
import com.bank.auth_service.entity.Role;
import com.bank.auth_service.entity.User;
import com.bank.auth_service.kafka.CreateAccountCustomerProducer;
import com.bank.auth_service.kafka.StatusProducer;
import com.bank.auth_service.mapper.UserMapper;
import com.bank.auth_service.redis.AuthRedisRepository;
import com.bank.auth_service.repository.RefreshTokenRepository;
import com.bank.auth_service.repository.RoleRepository;
import com.bank.auth_service.repository.UserRepository;
import com.bank.auth_service.security.CustomUserDetail;
import com.bank.auth_service.service.AuthService;
import com.bank.auth_service.service.RefreshTokenService;
import com.bank.auth_service.util.AuditUtil;
import com.bank.bank_common.client.OtpClient;
import com.bank.bank_common.dto.auth.response.RegisterRequestPhoneResponse;
import com.bank.bank_common.dto.event.StatusEvent;
import com.bank.bank_common.dto.event.UserActivatedEvent;
import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.ResendOtpRequest;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.bank_common.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final JwtService jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpClient otpClient;
    private final AuthRedisRepository authRedisRepository;
    private final AuditUtil auditUtil;
    private final CreateAccountCustomerProducer createAccountCustomerProducer;
    private final StatusProducer statusProducer;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        String username = loginRequest.getUsername();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {

            int attempts = authRedisRepository.increaseLoginAttempts(username);

            auditUtil.fail(null, username, "USER_LOGIN", "LOGIN",
                    "Wrong credentials - attempt: " + attempts);

            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        authRedisRepository.resetLoginAttempts(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != User.Status.ACTIVE) {
            auditUtil.fail(user.getId(), username, "USER_LOGIN", "LOGIN", "User inactive");
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        refreshTokenService.deleteByUserId(user.getId());

        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditUtil.success(user.getId(), username, "USER_LOGIN", "LOGIN", "Login success");

        return AuthResponse.builder()
                .message("Login successful")
                .user(userMapper.toResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public RegisterRequestPhoneResponse register(RegisterRequest request) {
// ✅ Email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        Optional<User> userOpt = userRepository.findByPhone(request.getPhone());
        if (userOpt.isPresent() && userOpt.get().getPhoneVerified()) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTER);
        }

        Optional<User> existingUserOpt = userRepository.findByPhone(request.getPhone());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.getPhoneVerified()) {
                auditUtil.fail(existingUser.getId(), existingUser.getUsername(),
                        "USER_REGISTER", "REGISTER", "Phone already registered");
                throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTER);
            }

            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            existingUser.setUsername(request.getUsername());

            userRepository.save(existingUser);

            sendOtp(request.getPhone());

            return RegisterRequestPhoneResponse.builder()
                    .message("OTP resent")
                    .status(200)
                    .build();
        }

        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setPhoneVerified(false);
        user.setStatus(User.Status.INACTIVE);

        userRepository.save(user);

        sendOtp(request.getPhone());

        auditUtil.success(user.getId(), user.getUsername(),
                "USER_REGISTER", "REGISTER", "User created, OTP sent");

        return RegisterRequestPhoneResponse.builder()
                .message("OTP sent")
                .status(201)
                .build();
    }

    private void sendOtp(String phone){
        try {
            otpClient.sendOtp(new SendOtpRequest(phone, OtpType.REGISTER));
        } catch (Exception e) {
            log.error("Send OTP failed", e);
        }
    }

    // ================= VERIFY =================
    @Override
    public AuthResponse verifyPhone(VerifyOtpRequest request) {

        User user = userRepository.findByPhone(request.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_WITH_PHONE));

        try {
            otpClient.verifyOtp(new VerifyOtpRequest(
                    request.getIdentifier(),
                    request.getOtp(),
                    OtpType.REGISTER
            ));
        } catch (Exception e) {
            auditUtil.fail(user.getId(), user.getUsername(),
                    "VERIFY_PHONE", "VERIFY", "OTP invalid");
            throw e;
        }

        return activateUserAfterVerify(user);
    }

    @Transactional
    public AuthResponse activateUserAfterVerify(User user) {

        if (Boolean.TRUE.equals(user.getPhoneVerified())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_VERIFIED);
        }

        user.setPhoneVerified(true);
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        // 🔥 EVENT (idempotent-safe)
        UserActivatedEvent event = new UserActivatedEvent();
        event.setUserId(user.getId());
        event.setPhone(user.getPhone());
        createAccountCustomerProducer.send(event);
        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditUtil.success(user.getId(), user.getUsername(),
                "VERIFY_PHONE", "VERIFY", "Phone verified");

        return AuthResponse.builder()
                .message("Phone verified successfully")
                .user(userMapper.toResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }


    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {

        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        User user = oldToken.getUser();
        refreshTokenRepository.delete(oldToken);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String newAccessToken = generateAccessToken(user);

        auditUtil.success(user.getId(), user.getUsername(),
                "REFRESH_TOKEN", "REFRESH", "Token refreshed");

        return AuthResponse.builder()
                .message("Token refreshed successfully")
                .user(userMapper.toResponse(user))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    private String generateAccessToken(User user) {
        CustomUserDetail userDetail = new CustomUserDetail(user);

        return jwtUtil.generateToken(
                userDetail.getUserId(),
                userDetail.getUsername(),
                userDetail.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    @Transactional
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == User.Status.BLOCKED) return;

        user.setStatus(User.Status.BLOCKED);
        userRepository.save(user);

        System.out.println("before producer");
        statusProducer.sendBlockAccount(new StatusEvent(
                UUID.randomUUID().toString(),
                userId,
                "USER_BLOCKED",
                LocalDateTime.now()
        ));
        System.out.println("after producer");
        auditUtil.success(user.getId(), user.getUsername(),
                "USER_BLOCK", "UPDATE", "User blocked");
    }

    @Transactional
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        statusProducer.sendUnblockAccount(new StatusEvent(
                UUID.randomUUID().toString(),
                userId,
                "USER_UNBLOCKED",
                LocalDateTime.now()
        ));

        auditUtil.success(user.getId(), user.getUsername(),
                "USER_UNBLOCK", "UPDATE", "User unblocked");
    }

    @Override
    public void resendOtp(String identifier) {
        identifier = identifier.trim().toLowerCase();

        ResendOtpRequest request = new ResendOtpRequest(
                identifier,
                OtpType.REGISTER
        );
        otpClient.resend(request);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // check old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            auditUtil.fail(userId, user.getUsername(),
                    "CHANGE_PASSWORD", "UPDATE", "Wrong old password");
            throw new BusinessException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        // check new password != old
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_DUPLICATED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 🔥 revoke all refresh tokens (force logout)
        refreshTokenService.deleteByUserId(userId);

        auditUtil.success(userId, user.getUsername(),
                "CHANGE_PASSWORD", "UPDATE", "Password changed");
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByPhone(request.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        try {
            otpClient.verifyOtp(new VerifyOtpRequest(
                    request.getIdentifier(),
                    request.getOtp(),
                    OtpType.FORGOT_PASSWORD
            ));
        } catch (Exception e) {
            auditUtil.fail(user.getId(), user.getUsername(),
                    "FORGOT_PASSWORD", "RESET", "OTP invalid");
            throw e;
        }

        String newPassword = request.getNewPassword();

        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MUST_BE_DIFFERENT);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        refreshTokenService.deleteByUserId(user.getId());

        auditUtil.success(user.getId(), user.getUsername(),
                "FORGOT_PASSWORD", "RESET", "Password reset success");
    }

    @Override
    public void sendForgotPasswordOtp(String phone) {
        try {
            otpClient.sendOtp(new SendOtpRequest(phone, OtpType.FORGOT_PASSWORD));
        } catch (Exception e) {
            log.error("Send OTP forgot password failed", e);
        }
    }

    @Override
    public void requestChangePhone(Long userId,
                                   ChangePhoneRequest request) {

        String newPhone = request.getNewPhone().trim();

        if (userRepository.existsByPhone(newPhone)) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTER);
        }

        authRedisRepository.savePendingPhoneChange(
                userId,
                newPhone
        );

        otpClient.sendOtp(
                new SendOtpRequest(
                        newPhone,
                        OtpType.CHANGE_PHONE
                )
        );
    }

    @Override
    @Transactional
    public void verifyChangePhone(Long userId,
                                  VerifyChangePhoneRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newPhone =
                authRedisRepository.getPendingPhoneChange(userId);

        if (newPhone == null) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND);
        }

        otpClient.verifyOtp(
                new VerifyOtpRequest(
                        newPhone,
                        request.getOtp(),
                        OtpType.CHANGE_PHONE
                )
        );

        user.setPhone(newPhone);
        user.setPhoneVerified(true);

        userRepository.save(user);

        authRedisRepository.deletePendingPhoneChange(userId);
    }
}
