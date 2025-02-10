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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        try {
            String jwt = parseBearerToken(request);
            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                // Token is valid
                String email = jwtProvider.getEmailFromToken(jwt);
                String rolesCsv = jwtProvider.getRolesFromToken(jwt); // e.g. "USER,ADMIN"
                List<SimpleGrantedAuthority> authorities = convertRolesToAuthorities(rolesCsv);

                // Build an Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Set it in context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            // We won't throw here; user will simply be unauthenticated
            // If you want to short-circuit, you can send an error response.
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

    private List<SimpleGrantedAuthority> convertRolesToAuthorities(String rolesCsv) {
        if (!StringUtils.hasText(rolesCsv)) {
            return List.of();
        }
        // rolesCsv is something like "USER,ADMIN"
        return Arrays.stream(rolesCsv.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r)) // Spring Security standard
                .collect(Collectors.toList());
    }
}
