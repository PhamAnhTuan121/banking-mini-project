package com.bank.otp_service.security;

import com.bank.bank_common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/otp/**").hasRole("INTERNAL")
                        .requestMatchers("/actuator/prometheus").permitAll()  // ✅ mở cho Prometheus
                        .requestMatchers("/actuator/health").permitAll()

                )
                .addFilterBefore(jwtAuthenticationFilter , UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}