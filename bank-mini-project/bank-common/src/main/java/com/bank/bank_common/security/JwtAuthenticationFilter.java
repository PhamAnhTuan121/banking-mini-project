package com.bank.bank_common.security;

import com.bank.bank_common.constant.SecurityConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ===== 1. INTERNAL SERVICE CALL =====
        String internalSource = request.getHeader(SecurityConstants.HEADER_INTERNAL);

        if (internalSource != null) {

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    internalSource,
                    null,
                    List.of(new SimpleGrantedAuthority(SecurityConstants.ROLE_INTERNAL))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            return;
        }

        // ===== 2. USER FROM API GATEWAY =====
        String userIdHeader = request.getHeader(SecurityConstants.HEADER_USER_ID);
        String rolesHeader = request.getHeader(SecurityConstants.HEADER_ROLES);

        if (userIdHeader != null && rolesHeader != null) {

            List<SimpleGrantedAuthority> authorities =
                    List.of(rolesHeader.split(","))
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(
                                    role.startsWith("ROLE_") ? role : "ROLE_" + role
                            ))
                            .toList();

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    Long.parseLong(userIdHeader),
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}