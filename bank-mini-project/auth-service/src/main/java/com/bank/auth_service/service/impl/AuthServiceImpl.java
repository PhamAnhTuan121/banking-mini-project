package com.bank.auth_service.service.impl;

import com.bank.auth_service.client.OtpClient;
import com.bank.auth_service.dto.request.LoginRequest;
import com.bank.auth_service.dto.request.RegisterRequest;
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
import com.bank.bank_common.dto.auth.response.RegisterRequestPhoneResponse;
import com.bank.bank_common.dto.event.StatusEvent;
import com.bank.bank_common.dto.event.UserActivatedEvent;
import com.bank.bank_common.dto.otp.OtpType;
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

        authRedisRepository.checkLoginRateLimit(username);
        int attempts = authRedisRepository.getLoginAttempts(username);

        if (attempts > 5) {
            auditUtil.fail(null, username, "USER_LOGIN", "LOGIN", "Too many attempts");
            throw new BusinessException(ErrorCode.TOO_MANY_LOGIN_ATTEMPTS);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            auditUtil.fail(null, username, "USER_LOGIN", "LOGIN", "Wrong credentials");
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
                .status(200)
                .user(userMapper.toResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public RegisterRequestPhoneResponse register(RegisterRequest request) {

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
        event.setFullName(user.getUsername());

        try {
            createAccountCustomerProducer.send(event);
        } catch (Exception e) {
            log.error("Kafka send failed", e);
        }

        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditUtil.success(user.getId(), user.getUsername(),
                "VERIFY_PHONE", "VERIFY", "Phone verified");

        return AuthResponse.builder()
                .message("Phone verified successfully")
                .status(200)
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
                .status(200)
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

        if (user.getStatus() == User.Status.BLOCKED) return; // idempotent

        user.setStatus(User.Status.BLOCKED);
        userRepository.save(user);

        // 🔥 publish event
        statusProducer.sendBlockAccount(new StatusEvent(
                UUID.randomUUID().toString(),
                userId,
                "USER_BLOCKED",
                LocalDateTime.now()
        ));

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
}
