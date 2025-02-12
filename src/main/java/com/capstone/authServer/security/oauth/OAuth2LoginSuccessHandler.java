package com.capstone.authServer.security.oauth;

import com.capstone.authServer.model.User;
import com.capstone.authServer.security.jwt.JwtProvider;
import com.capstone.authServer.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    // e.g., "http://localhost:5173" — adjust as needed
    private static final String FRONTEND_REDIRECT_URL = "http://localhost:5173/oauth2/success";

    public OAuth2LoginSuccessHandler(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String email = authentication.getName();
        if (!StringUtils.hasText(email)) {
            // fallback
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No email found from OAuth provider");
            return;
        }

        // Retrieve user from DB
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth login"));

        // Determine user’s default tenant & role
        Long tenantId = user.getDefaultTenantId();
        String roleName = userService.findUserRoleInDefaultTenant(user);

        // Generate JWT
        String token = jwtProvider.generateToken(email, tenantId, roleName);

        // e.g. redirect to your React app with token
        String redirectUrl = FRONTEND_REDIRECT_URL + "?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
