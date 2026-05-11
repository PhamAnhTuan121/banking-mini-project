package com.bank.bank_common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.bank")
@EnableConfigurationProperties
@EnableFeignClients
public class BankCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankCommonApplication.class, args);
	}

}
