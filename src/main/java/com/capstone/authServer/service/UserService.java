package com.capstone.authServer.service;

import com.capstone.authServer.model.Role;
import com.capstone.authServer.model.User;
import com.capstone.authServer.repository.RoleRepository;
import com.capstone.authServer.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Upsert user from OAuth (Google).
     */
    public User handleOAuth2User(String oauthId, String email, String name, String imageUrl, String provider) {
        final Logger logger = LoggerFactory.getLogger(UserService.class);
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        logger.info("Current received");
        logger.info("OauthId: " + oauthId);
        logger.info("Email: " + email);
        logger.info("Name: " + name);
        logger.info("Image: " + imageUrl);
        logger.info("Provider: " + provider);

        logger.info("Existing user: " + existingUserOpt.isPresent());
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
            logger.info("CREATING NEW USER....");
            // Create new
            user = new User();
            user.setOauthId(oauthId);
            user.setEmail(email);
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setProvider(provider);
            user.setCreatedAt(Timestamp.from(Instant.now()));
            user.setUpdatedAt(Timestamp.from(Instant.now()));

            // Assign default role "USER" or any role logic you want
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
            user.getRoles().add(userRole);
            logger.info("NEWLY CREATED USER: " + user.toString());
        }

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
