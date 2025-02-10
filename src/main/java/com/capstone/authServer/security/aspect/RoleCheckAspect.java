package com.capstone.authServer.security.aspect;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.security.JwtAuthenticationFilter;
import com.capstone.authServer.security.annotation.AllowedRoles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class RoleCheckAspect {

    @Around("@within(allowedRoles) || @annotation(allowedRoles)")
    public Object checkRoles(ProceedingJoinPoint pjp, AllowedRoles allowedRoles) throws Throwable {

        final Logger logger = LoggerFactory.getLogger(RoleCheckAspect.class);
        logger.info("INSIDE THE ROLE CHECK ASPECT");
        logger.info("ALLOWED ROLES: " + allowedRoles);
        // If no annotation is present or no roles specified, proceed normally
        if (allowedRoles == null || allowedRoles.value().length == 0) {
            return pjp.proceed();
        }

        // Retrieve roles required by the annotation
        List<String> requiredRoles = Arrays.asList(allowedRoles.value());

        // Check roles from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.error(401, "Unauthorized: No valid authentication."));
        }

        // Convert to a list of role names, e.g. "ADMIN", "SUPER_ADMIN", etc.
        List<String> userRoles = auth.getAuthorities().stream()
                .map(grantedAuthority -> {
                    // Usually the authority is "ROLE_ADMIN" => remove "ROLE_"
                    String authority = grantedAuthority.getAuthority();
                    if (authority.startsWith("ROLE_")) {
                        authority = authority.substring(5);
                    }
                    return authority;
                })
                .collect(Collectors.toList());

        // Check if user has at least one required role
        boolean hasRequiredRole = userRoles.stream()
                .anyMatch(requiredRoles::contains);

        if (!hasRequiredRole) {
            // Return 403
            return ResponseEntity
                    .status(403)
                    .body(ApiResponse.error(403, "Forbidden: Insufficient roles. Required Roles: " + requiredRoles));
        }

        // If all checks pass, proceed
        return pjp.proceed();
    }
}
