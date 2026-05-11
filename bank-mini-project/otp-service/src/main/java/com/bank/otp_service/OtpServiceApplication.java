package com.bank.otp_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
		"com.bank.otp_service",
		"com.bank.bank_common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class OtpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtpServiceApplication.class, args);
	}

}
