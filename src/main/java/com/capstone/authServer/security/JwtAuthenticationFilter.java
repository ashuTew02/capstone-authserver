package com.capstone.authServer.security;

import com.capstone.authServer.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, jakarta.servlet.ServletException {

        try {
            String jwt = parseBearerToken(request);
            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                // Extract needed data
                String email = jwtProvider.getEmailFromToken(jwt);
                Long tenantId = jwtProvider.getTenantIdFromToken(jwt);
                String role = jwtProvider.getRoleFromToken(jwt); // single role, e.g. "USER"

                // Build a list of authorities. In Spring, roles typically start with "ROLE_"
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                List<SimpleGrantedAuthority> authorities = List.of(authority);

                // Build an Authentication object
                // We’ll store the email as the principal, but you could store a custom UserPrincipal
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // If you want the tenantId accessible, you can store it in details:
                authentication.setDetails(tenantId);

                // Set it in context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            // Optionally short-circuit with error response
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
