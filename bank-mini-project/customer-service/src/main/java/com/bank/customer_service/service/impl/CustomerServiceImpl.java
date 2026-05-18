package com.bank.customer_service.service.impl;

import com.bank.customer_service.client.AccountClient;
import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.bank_common.dto.customer.response.UserProfileResponse;
import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.customer_service.client.AuthClient;
import com.bank.customer_service.client_wrapper.AuthService;
import com.bank.customer_service.client_wrapper.OtpService;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.KycStatus;
import com.bank.customer_service.entity.Status;
import com.bank.customer_service.mapper.CustomerMapper;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerService;
import com.bank.customer_service.service.InternalCustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService, InternalCustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final OtpService otpService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PHONE_PENDING = "customer:phone:";
    private final AuthService authService;
    private final AccountClient accountClient;
    private final AuthClient authClient;

    @Override
    public void blockUser(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
        customer.setStatus(Status.BLOCKED);
        customerRepository.save(customer);
    }

    @Override
    public void unblock(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
        customer.setStatus(Status.ACTIVE);
        customerRepository.save(customer);
    }

    @Override
    public void createDefaultCustomer(Long userId) {
        if (customerRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_ALREADY_EXISTS);
        }
        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setStatus(Status.ACTIVE);
        customer.setKycStatus(KycStatus.VERIFIED);
        customer.setDailyLimit(resolveDailyLimit(customer.getKycStatus()));
        customer.setUsedToday(new BigDecimal(0));
        customerRepository.save(customer);
    }

    private BigDecimal resolveDailyLimit(KycStatus kycStatus) {
        switch (kycStatus) {
            case VERIFIED:
                return new BigDecimal("100000000");
            case PENDING:
            default:
                return new BigDecimal("10000000");
        }
    }

    @Override
    public UserProfileResponse getByUserId(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        var user = authClient.getUser(userId);
        var account = accountClient.getByUserId(userId);
        System.out.println("Account number " + account.getAccountNumber());
        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .phone(user.getPhone())

                .fullName(user.getFullName())
                .address(customer.getAddress())
                .email(user.getEmail())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();

    }

    @Override
    public CustomerResponse updateProfile(Long userId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
//        if(request.getFullName() != null){
//            customer.setFullName(request.getFullName());
//        }
        if(request.getAddress() != null){
            customer.setAddress(request.getAddress());
        }
        customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    public void requestPhoneOtp(Long userId, String phone) {

        String cleanPhone = phone.trim(); // ✅ FIX

        redisTemplate.opsForValue()
                .set(PHONE_PENDING + userId, cleanPhone, 5, TimeUnit.MINUTES);

        otpService.sendOtp(new SendOtpRequest(cleanPhone, OtpType.VERIFY_PHONE));

        System.out.println("Phone request: [" + cleanPhone + "]");
    }

    @Override
    public void verifyPhoneOtp(Long userId, String otp) {

        String phone = redisTemplate.opsForValue().get(PHONE_PENDING + userId);

        if (phone == null) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND);
        }

        String cleanPhone = phone.trim();

        otpService.verifyOtp(
                phone,
                otp,
                OtpType.VERIFY_PHONE

        );

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        authService.updatePhone(userId, cleanPhone);
        customer.setKycStatus(KycStatus.VERIFIED);
        customerRepository.save(customer);

        redisTemplate.delete(PHONE_PENDING + userId);
    }

    @Override
    public void requestOldPhoneOtp(Long userId) {

        String oldPhone = authService.getPhone(userId);

        if (oldPhone == null || oldPhone.isBlank()) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND);
        }

        otpService.sendOtp(new SendOtpRequest(oldPhone, OtpType.VERIFY_OLD_PHONE));
    }

    private static final String PHONE_CHANGE_ALLOWED = "customer:phone:allow:";

    @Override
    public void verifyOldPhoneOtp(Long userId, String otp) {

        String oldPhone = authService.getPhone(userId);

        if (oldPhone == null || oldPhone.isBlank()) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND);
        }

        otpService.verifyOtp(oldPhone, otp,OtpType.VERIFY_OLD_PHONE);

        redisTemplate.opsForValue()
                .set(PHONE_CHANGE_ALLOWED + userId, "true", 5, TimeUnit.MINUTES);
    }

}
