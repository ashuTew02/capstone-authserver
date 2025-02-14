package com.capstone.authServer.security;

import com.capstone.authServer.model.User;
import com.capstone.authServer.model.UserTenantMapping;
import com.capstone.authServer.repository.UserRepository;
import com.capstone.authServer.repository.UserTenantMappingRepository;
import com.capstone.authServer.security.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserTenantMappingRepository userTenantMappingRepository;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider,
            UserRepository userRepository,
            UserTenantMappingRepository userTenantMappingRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userTenantMappingRepository = userTenantMappingRepository;
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
                // 1) Extract email & tenantId from token (ignore role from token).
                String email = jwtProvider.getEmailFromToken(jwt);
                Long tenantId = jwtProvider.getTenantIdFromToken(jwt);

                // 2) Look up the user
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    logger.warn("No user found for email={} => skipping auth", email);
                    filterChain.doFilter(request, response);
                    return;
                }
                User user = userOpt.get();

                // 3) Check user-tenant mapping => get actual role from DB
                Optional<UserTenantMapping> mappingOpt =
                        userTenantMappingRepository.findByUserIdAndTenantId(user.getId(), tenantId);

                if (mappingOpt.isEmpty()) {
                    logger.warn("User {} is not mapped to tenant {}, ignoring token", email, tenantId);
                    // e.g. short-circuit or just pass along as anonymous
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4) We have a valid user-tenant mapping => get real role
                String realRoleName = mappingOpt.get().getRole().getName(); // e.g. "ADMIN", "SUPER_ADMIN"

                // 5) Build authorities: ROLE_<realRoleName>
                SimpleGrantedAuthority authority = 
                        new SimpleGrantedAuthority("ROLE_" + realRoleName);
                List<SimpleGrantedAuthority> authorities = List.of(authority);

                // 6) Build an Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(), // principal
                                null,           // no credentials
                                authorities
                        );

                // store the tenantId in details if you want
                authentication.setDetails(tenantId);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
            // Optionally send error response or continue filter chain as anonymous
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            // Optionally handle
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
