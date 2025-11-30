package com.cgl.userservice.utils;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.services.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtControlTest {

    @Mock
    private JwtTools jwtTools;

    @Mock
    private LoginService loginService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtControl jwtControl;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role(Role.CLIENT)
                .build();

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_LoginEndpoint_SkipsJwtValidation() throws ServletException, IOException {
        // Condition 1: requestPath equals /api/v1/auth/login
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader("Authorization");
        verifyNoInteractions(jwtTools, loginService);
    }

    @Test
    void testDoFilterInternal_RegisterEndpoint_SkipsJwtValidation() throws ServletException, IOException {
        // Condition 2: requestPath equals /api/v1/auth/register
        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader("Authorization");
        verifyNoInteractions(jwtTools, loginService);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Condition 3: header == null
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtTools, loginService);
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // Condition 4: !header.startsWith("Bearer ")
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Basic credentials");

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtTools, loginService);
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // All conditions pass - complete flow
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTools.extractEmail(token)).thenReturn("test@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginService.loadUserByUsername("test@example.com")).thenReturn(testUser);
        when(jwtTools.validateToken(token, testUser)).thenReturn(true);
        doReturn(authorities).when(jwtTools).extractAuthorities(token);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).extractEmail(token);
        verify(loginService, times(1)).loadUserByUsername("test@example.com");
        verify(jwtTools, times(1)).validateToken(token, testUser);
        verify(jwtTools, times(1)).extractAuthorities(token);
        verify(securityContext, times(1)).setAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExtractEmailReturnsNull() throws ServletException, IOException {
        // Condition: email == null
        String token = "invalid.jwt.token";
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenReturn(null);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).extractEmail(token);
        verify(loginService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthenticationAlreadyExists() throws ServletException, IOException {
        // Condition: getAuthentication() != null
        String token = "valid.jwt.token";
        Authentication existingAuth = mock(Authentication.class);

        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenReturn("test@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).extractEmail(token);
        verify(loginService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Condition: validateToken returns false
        String token = "invalid.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenReturn("test@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginService.loadUserByUsername("test@example.com")).thenReturn(testUser);
        when(jwtTools.validateToken(token, testUser)).thenReturn(false);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).validateToken(token, testUser);
        verify(jwtTools, never()).extractAuthorities(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExceptionDuringProcessing() throws ServletException, IOException {
        // Catch block - exception thrown
        String token = "problematic.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenThrow(new RuntimeException("Token parsing error"));

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(loginService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_TokenSubstring() throws ServletException, IOException {
        // Verify substring(7) extracts token correctly
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer 1234567890abcdef");
        when(jwtTools.extractEmail("1234567890abcdef")).thenReturn(null);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).extractEmail("1234567890abcdef");
    }

    @Test
    void testDoFilterInternal_WithSellerRole() throws ServletException, IOException {
        // Test with SELLER role
        String token = "seller.jwt.token";
        User seller = User.builder()
                .id("seller-1")
                .email("seller@example.com")
                .role(Role.SELLER)
                .build();

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_SELLER"));

        when(request.getRequestURI()).thenReturn("/api/v1/products");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenReturn("seller@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginService.loadUserByUsername("seller@example.com")).thenReturn(seller);
        when(jwtTools.validateToken(token, seller)).thenReturn(true);
        doReturn(authorities).when(jwtTools).extractAuthorities(token);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(1)).setAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_DifferentProtectedEndpoint() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/products");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_LogsRequestPath() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/test/endpoint");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getRequestURI();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_MultipleAuthorities() throws ServletException, IOException {
        String token = "multi.authority.token";
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT"),
                new SimpleGrantedAuthority("ROLE_PREMIUM")
        );

        when(request.getRequestURI()).thenReturn("/api/v1/premium/feature");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTools.extractEmail(token)).thenReturn("premium@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(loginService.loadUserByUsername("premium@example.com")).thenReturn(testUser);
        when(jwtTools.validateToken(token, testUser)).thenReturn(true);
        doReturn(authorities).when(jwtTools).extractAuthorities(token);

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(jwtTools, times(1)).extractAuthorities(token);
        verify(securityContext, times(1)).setAuthentication(any());
    }

    @Test
    void testDoFilterInternal_EmptyBearerToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtControl.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}

