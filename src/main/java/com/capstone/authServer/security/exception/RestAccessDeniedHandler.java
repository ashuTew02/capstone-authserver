package com.capstone.authServer.security.exception;

import com.capstone.authServer.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request, 
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<?> apiResp = ApiResponse.error(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden: " + accessDeniedException.getMessage()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), apiResp);
    }
}
