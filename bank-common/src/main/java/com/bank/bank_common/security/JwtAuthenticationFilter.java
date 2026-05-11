package com.bank.bank_common.security;

import com.bank.bank_common.constant.SecurityConstants;
import com.bank.bank_common.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String internalToken =
                    request.getHeader(SecurityConstants.HEADER_INTERNAL);
            if (SecurityConstants.INTERNAL_SECRET.equals(internalToken)) {
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                "internal-service",
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                SecurityConstants.ROLE_INTERNAL
                                        )
                                )
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                if (jwtService.validateToken(token)) {

                    Long userId =
                            jwtService.extractUserId(token);

                    List<String> roles =
                            jwtService.extractRoles(token);

                    List<SimpleGrantedAuthority> authorities =
                            roles.stream()
                                    .map(role -> {

                                        String normalizedRole =
                                                role.toUpperCase();

                                        return new SimpleGrantedAuthority(
                                                normalizedRole.startsWith("ROLE_")
                                                        ? normalizedRole
                                                        : "ROLE_" + normalizedRole
                                        );
                                    })
                                    .toList();

                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(auth);

                    System.out.println("AUTH OK: " + authorities);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}