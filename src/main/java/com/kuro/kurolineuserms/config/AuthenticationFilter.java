package com.kuro.kurolineuserms.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kuro.kurolineuserms.data.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        if (path.contains("/register")) {
            // Skip authentication for this path
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String newContent = "{\"message\": \"An authentication token must be provided.\"}";
            response.setContentLength(newContent.length());
            response.getOutputStream().write(newContent.getBytes());
            return;
        }

        final String token = authHeader.substring(7);
        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String newContent = String.format("{\"message\": \"%s\"}", ex.getMessage());
            response.setContentLength(newContent.length());
            response.getOutputStream().write(newContent.getBytes());
            return;
        }
        User user = new User();
        user.setId(decodedToken.getUid());
        user.setName(decodedToken.getName());
        user.setEmail(decodedToken.getEmail());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
