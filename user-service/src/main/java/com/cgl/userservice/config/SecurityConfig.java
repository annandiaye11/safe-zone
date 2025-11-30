package com.cgl.userservice.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cgl.userservice.utils.JwtControl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtControl jwtFilter;

    @Bean
    @SuppressWarnings("java:S4502") // CSRF disabled: safe for stateless JWT-based REST API (see SECURITY-CSRF-JUSTIFICATION.md)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                /*
                 * CSRF Protection is disabled for the following reasons:
                 * 1. This is a stateless REST API using JWT tokens for authentication
                 * 2. No session cookies are used (SessionCreationPolicy.STATELESS)
                 * 3. JWT tokens are sent in Authorization header, not in cookies
                 * 4. CSRF attacks target cookie-based authentication, which we don't use
                 *
                 * Security measures in place:
                 * - JWT token validation on each request
                 * - Stateless session management
                 * - Token expiration
                 * - Secure token generation and validation (see JwtTools)
                 *
                 * This configuration is safe for REST APIs as per OWASP recommendations:
                 * https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
                 */
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - authentication not required
                        .requestMatchers(
                                "/api/v1/auth/**",          // Authentication endpoints
                                "/api/v1/users/register",   // User registration
                                "/actuator/health",         // Health check
                                "/actuator/info",           // Application info
                                "/v3/api-docs/**",          // OpenAPI docs
                                "/swagger-ui/**",           // Swagger UI
                                "/swagger-ui.html"          // Swagger UI HTML
                        ).permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(ApplicationContext context) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setApplicationContext(context);

        return expressionHandler;
    }

}
