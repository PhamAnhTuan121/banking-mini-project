package com.bank.customer_service.service.impl;

import com.bank.bank_common.dto.customer.request.CustomerUpdateRequest;
import com.bank.bank_common.dto.customer.response.CustomerResponse;
import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.KycStatus;
import com.bank.customer_service.entity.Status;
import com.bank.customer_service.mapper.CustomerMapper;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerService;
import com.bank.customer_service.service.InternalCustomerService;
import com.bank.customer_service.service.OtpClient;
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
    private final OtpClient otpClient;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PHONE_PENDING = "customer:phone:";

    @Override
    public void createDefaultCustomer(Long userId, String fullName, String phoneNumber) {
        if (customerRepository.existsByUserId(userId)) {
            System.out.println("Customer already exists");
            return;
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name must not be null");
        }
        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setFullName(fullName);
        customer.setStatus(Status.ACTIVE);
        customer.setPhone(phoneNumber);
        customer.setKycStatus(KycStatus.PENDING);
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
    public CustomerResponse getByUserId(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateProfile(Long userId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setFullName(request.getFullName());
        customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    public void blockCustomer(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus(Status.BLOCKED);
        customerRepository.save(customer);
    }

    @Override
    public void requestPhoneOtp(Long userId, String phone) {
        if(customerRepository.existsByPhone(phone)){
            throw new RuntimeException("Phone already exists");
        }

        redisTemplate.opsForValue()
                .set(PHONE_PENDING + userId, phone, 5, TimeUnit.MINUTES);
        otpClient.sendOtp(new SendOtpRequest(phone, OtpType.REGISTER));
    }

    @Override
    public void verifyPhoneOtp(Long userId, String otp) {
        String phone = redisTemplate.opsForValue().get(PHONE_PENDING + userId);
        if (phone == null) {
            throw new RuntimeException("OTP expired");
        }
        otpClient.verifyOtp(
                new VerifyOtpRequest(phone,otp, OtpType.REGISTER)
        );
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setPhone(phone);
        customer.setPhoneVerified(true);
        customer.setKycStatus(KycStatus.VERIFIED);
        customerRepository.save(customer);
        redisTemplate.delete(PHONE_PENDING + userId);
    }


}
