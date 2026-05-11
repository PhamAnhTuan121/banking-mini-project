package com.bank.account_service.security;

import com.bank.bank_common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/internal/**")
                .hasAnyRole("INTERNAL", "ADMIN")

                .requestMatchers("/api/v1/admin/**")
                .hasRole("ADMIN")

                .requestMatchers("/api/v1/employee/**")
                .hasAnyRole("EMPLOYEE", "ADMIN")

                .requestMatchers("/api/v1/accounts/**")
                .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers("/actuator/prometheus")
                .permitAll()

                .requestMatchers("/actuator/health")
                .permitAll()

                .anyRequest()
                .authenticated()
        );

        return http.build();
    }
}