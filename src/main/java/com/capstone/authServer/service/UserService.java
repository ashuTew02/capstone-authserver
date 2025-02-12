package com.capstone.authServer.service;

import com.capstone.authServer.model.Role;
import com.capstone.authServer.model.Tenant;
import com.capstone.authServer.model.User;
import com.capstone.authServer.model.UserTenantMapping;
import com.capstone.authServer.repository.RoleRepository;
import com.capstone.authServer.repository.TenantRepository;
import com.capstone.authServer.repository.UserRepository;
import com.capstone.authServer.repository.UserTenantMappingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserTenantMappingRepository userTenantMappingRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            TenantRepository tenantRepository,
            UserTenantMappingRepository userTenantMappingRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tenantRepository = tenantRepository;
        this.userTenantMappingRepository = userTenantMappingRepository;
    }

    /**
     * Upsert user from OAuth2 (e.g., Google). 
     * By default, if new user, assign them to tenant=1 with role=USER, 
     * and also set user’s defaultTenantId=1.
     */
    public User handleOAuth2User(String oauthId, String email, String name, String imageUrl, String provider) {
        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            // Update existing
            user = existingUserOpt.get();
            user.setOauthId(oauthId);
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setProvider(provider);
            user.setUpdatedAt(Timestamp.from(Instant.now()));
        } else {
            logger.info("Creating NEW USER for email={}", email);
            // Create new
            user = new User();
            user.setOauthId(oauthId);
            user.setEmail(email);
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setProvider(provider);
            user.setCreatedAt(Timestamp.from(Instant.now()));
            user.setUpdatedAt(Timestamp.from(Instant.now()));

            // Set default tenant to 1
            user.setDefaultTenantId(1L);
        }

        // Save or update user
        User savedUser = userRepository.save(user);

        // Ensure the user has a mapping with tenant=1 and role=USER
        // (you might skip if it already exists)
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found!"));
        Tenant defaultTenant = tenantRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default Tenant (id=1) not found!"));
        
        Optional<UserTenantMapping> mappingOpt = userTenantMappingRepository
                .findByUserIdAndTenantId(savedUser.getId(), 1L);

        if (mappingOpt.isEmpty()) {
            UserTenantMapping mapping = new UserTenantMapping();
            mapping.setUser(savedUser);
            mapping.setTenant(defaultTenant);
            mapping.setRole(userRole);
            userTenantMappingRepository.save(mapping);
        }

        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieve the role name for the user in their default tenant.
     * Returns e.g. "ADMIN", "USER", etc.
     */
    public String findUserRoleInDefaultTenant(User user) {
        Long defaultTid = user.getDefaultTenantId();
        if (defaultTid == null) {
            // fallback: or throw an exception
            return "UNKNOWN";
        }
        Optional<UserTenantMapping> mappingOpt = userTenantMappingRepository
                .findByUserIdAndTenantId(user.getId(), defaultTid);
        return mappingOpt
                .map(m -> m.getRole().getName())
                .orElse("UNKNOWN");
    }

        /**
     * Returns the specific user-tenant mapping if it exists.
     */
    public Optional<UserTenantMapping> findUserTenantMapping(Long userId, Long tenantId) {
        return userTenantMappingRepository.findByUserIdAndTenantId(userId, tenantId);
    }

    /**
    * If you want to change the user’s default tenant in the DB:
    */
    public void updateUserDefaultTenant(User user, Long newTenantId) {
        user.setDefaultTenantId(newTenantId);
        userRepository.save(user);
    }
}
