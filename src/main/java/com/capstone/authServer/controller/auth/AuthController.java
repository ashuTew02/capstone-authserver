package com.capstone.authServer.controller.auth;

import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.model.User;
import com.capstone.authServer.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This is a no-op for JWT; the client should remove the token.
     * We'll just return a success message.
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success(HttpStatus.OK.value(), "Logged out (client must discard token)", null);
    }

    /**
     * Return minimal user info. We have an authentication object from SecurityContext
     * if the token was valid. 
     */
    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Not authenticated");
        }

        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "User not found");
        }

        User user = userOpt.get();
        // Return partial info
        return ApiResponse.success(HttpStatus.OK.value(), "Fetched current user", user);
    }
}
