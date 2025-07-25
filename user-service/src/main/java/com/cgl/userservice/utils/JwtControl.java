package com.cgl.userservice.utils;


import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.services.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
public class JwtControl extends OncePerRequestFilter {

    private final JwtTools jwtTools;
    private final LoginService loginService;
    public JwtControl(JwtTools jwtTools, LoginService loginService) {
        this.jwtTools = jwtTools;
        this.loginService = loginService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        System.out.println("--- Request path: " + requestPath + "");
        // Ignorer le filtre JWT pour les endpoints publics
        if ("/api/login".equals(requestPath) || "/users/register".equals(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String email = jwtTools.extractEmail(token);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = loginService.loadUserByUsername(email);
                    User user = (User) userDetails;
                    if (jwtTools.validateToken(token, user)) {
                        Collection<? extends GrantedAuthority> authorities = jwtTools.extractAuthorities(token);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du token JWT: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}