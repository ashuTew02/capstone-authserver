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
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    // e.g., "http://localhost:5173" -- Adjust as needed
    private static final String FRONTEND_REDIRECT_URL = "http://localhost:5173/oauth2/success";

    public OAuth2LoginSuccessHandler(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        /*
         * By the time this handler is called, the CustomOidcUserService has stored
         * the user in DB. We can retrieve the user’s email from the “authentication.getName()”
         * or from the OAuth2User attributes.
         */
        String email = authentication.getName();
        if (!StringUtils.hasText(email)) {
            // fallback - shouldn't happen if Google returns an email
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No email found from OAuth provider");
            return;
        }

        // Retrieve user from DB to get roles
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth login"));

        // Convert user roles to CSV
        String rolesCsv = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.joining(","));

        // Generate JWT
        String token = jwtProvider.generateToken(user.getEmail(), rolesCsv);

        // Option 1: Redirect to your frontend with token as a query param:
        String redirectUrl = FRONTEND_REDIRECT_URL + "?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
