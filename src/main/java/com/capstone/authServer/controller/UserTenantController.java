package com.capstone.authServer.controller;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.dto.response.CurrentTenantDTO;
import com.capstone.authServer.model.Tenant;
import com.capstone.authServer.model.User;
import com.capstone.authServer.model.UserTenantMapping;
import com.capstone.authServer.repository.TenantRepository;
import com.capstone.authServer.repository.UserTenantMappingRepository;
import com.capstone.authServer.security.jwt.JwtProvider;
import com.capstone.authServer.service.UserService;
import com.capstone.authServer.dto.UserTenantDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserTenantController {

    // or wherever your frontend wants to handle the new token

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TenantRepository tenantRepository;
    private final UserTenantMappingRepository userTenantMappingRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserTenantController.class);

    public UserTenantController(UserService userService, JwtProvider jwtProvider, TenantRepository tenantRepository, UserTenantMappingRepository userTenantMappingRepository) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.tenantRepository = tenantRepository;
        this.userTenantMappingRepository = userTenantMappingRepository;
    }

    /**
     * Lists all tenants the currently logged-in user is a member of, 
     * along with the user’s role in each tenant.
     *
     * Example response:
     * [
     *   { "tenantId": 1, "tenantName": "Default Tenant", "roleName": "USER" },
     *   { "tenantId": 2, "tenantName": "ACME Corp", "roleName": "ADMIN" }
     * ]
     */
    @GetMapping("/tenants")
    public ResponseEntity<ApiResponse<List<UserTenantDTO>>> getUserTenants() {
        // 1) Identify the current user from SecurityContext
        logger.info("INSIDE GET USER TENANTS CONTROLLER");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // we stored email as principal in the JwtAuthenticationFilter

        if (!StringUtils.hasText(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "No user is logged in!"));
        }

        // 2) Look up the user & their tenant mappings
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }

        // 3) Convert userTenantMappings to a DTO
        List<UserTenantDTO> tenantDtos = user.getUserTenantMappings().stream()
                .map(mapping -> {
                    // mapping.getTenant().getId(), etc.
                    return new UserTenantDTO(
                            mapping.getTenant().getId(),
                            mapping.getTenant().getName(),
                            mapping.getRole().getName()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Successfully retrieved user tenants.",
                tenantDtos
            )
        );
    }

    /**
     * Switch to a new tenant, if the user is part of that tenant.
     * Returns a redirect to the frontend with a new JWT (or could return JSON if you prefer).
     *
     * Example request: POST /api/user/tenant/switch?tenantId=2
     * Then it redirects to "http://localhost:5173/tenantSwitch/success?token=..."
     */
    @GetMapping("/tenant/switch")
    public ApiResponse<?> switchTenant(@RequestParam("tenantId") Long tenantId) throws java.io.IOException {
        // 1) Identify the current user from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        if (!StringUtils.hasText(email)) {
            // Usually you'd return a 401, but you mentioned wanting to redirect. 
            // For simplicity, let's just do a 401:
            throw new RuntimeException("No user is logged in!");
        }

        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) Verify user belongs to that tenant
        // If user has multiple roles, pick one. By default, we pick the first or a specific one.
        UserTenantMapping mapping = userService.findUserTenantMapping(user.getId(), tenantId)
            .orElseThrow(() -> new RuntimeException("User is not a member of tenant " + tenantId));

        // 3) Generate a NEW JWT for that tenant
        String newToken = jwtProvider.generateToken(
                user.getEmail(),
                tenantId
        );

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("token", newToken);
        return ApiResponse.success(HttpStatus.OK.value(), "Successfully switched to tenant " + tenantId, response);

    }

    @GetMapping("/tenant/current")
    public ResponseEntity<ApiResponse<CurrentTenantDTO>> getCurrentTenant() {
        // 1) Identify the user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(
                            HttpStatus.UNAUTHORIZED.value(),
                            "No user is logged in!"
                    ));
        }

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            HttpStatus.NOT_FOUND.value(),
                            "User not found"
                    ));
        }

        // 2) Retrieve the “active” tenantId from authentication details (set in JwtAuthenticationFilter)
        Object details = auth.getDetails();
        if (!(details instanceof Long)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            HttpStatus.BAD_REQUEST.value(),
                            "No tenantId found in auth details"
                    ));
        }
        Long tenantId = (Long) details;

        // 3) Lookup the Tenant
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            HttpStatus.NOT_FOUND.value(),
                            "Tenant not found for tenantId=" + tenantId
                    ));
        }

        // 4) Find the user’s role in this tenant
        Optional<UserTenantMapping> mappingOpt =
                userTenantMappingRepository.findByUserIdAndTenantId(user.getId(), tenantId);
        if (mappingOpt.isEmpty()) {
            // The user is not a member of this tenant (shouldn’t happen if token is correct)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(
                            HttpStatus.FORBIDDEN.value(),
                            "User is not a member of tenantId=" + tenantId
                    ));
        }
        String roleName = mappingOpt.get().getRole().getName();

        // 5) Build response DTO
        CurrentTenantDTO tenantDTO = new CurrentTenantDTO(
                tenant.getId(),
                tenant.getName(),
                roleName
        );

        // 6) Return success
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                "Current tenant retrieved successfully.",
                tenantDTO
            )
        );
    }
}
