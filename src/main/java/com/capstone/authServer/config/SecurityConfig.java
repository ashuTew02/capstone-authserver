package com.capstone.authServer.config;

import com.capstone.authServer.security.JwtAuthenticationFilter;
import com.capstone.authServer.security.exception.RestAuthenticationEntryPoint;
import com.capstone.authServer.security.exception.RestAccessDeniedHandler;
import com.capstone.authServer.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF, use tokens instead
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())

            // Exception handling for unauthenticated / forbidden
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        // "/auth/**", 
                        "/oauth2/**"
                        // "/dashboard/**",
                        // "/api/github/**"
                    ).permitAll()
                // any other public endpoints if needed
                .anyRequest().authenticated()
            )

            // Configure OAuth2 login 
            .oauth2Login(oauth -> oauth
                // .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
            )

            // No form login (we rely on OAuth2 or JWT)
            .formLogin(form -> form.disable())

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("http://localhost:5173") 
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        // Add our custom JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



// "/auth/**",
// "/oauth2/**",
// // You can open certain controllers publicly if desired
// "/dashboard/**",
// "/api/github/**"