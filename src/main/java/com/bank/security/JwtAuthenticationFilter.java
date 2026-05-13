package com.bank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Signs and validates tokens for this filter.
    private final UserDetailsService userDetailsService; // Reloads roles/state from DB on each JWT-authenticated request.

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response ,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // Standard place REST clients send Bearer tokens.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) { // No JWT path: leave security context unchanged (session or anonymous).
            filterChain.doFilter(request, response); // Continue chain so form-login sessions still work.
            return;
        }
        String jwt = authHeader.substring(7); // Strips "Bearer " prefix to raw compact JWS.
        try {
            String username = jwtService.extractUsername(jwt); // Subject must resolve to a user.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Avoid overriding an existing authentication (e.g. session).
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Fresh authorities from persistence.
                if (jwtService.isTokenValid(jwt, userDetails)) { // Signature, expiry, and subject alignment checks.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()); // Pre-authenticated token for SecurityContext.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Binds remote IP/session id for auditing.
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Makes @PreAuthorize and MVC security see the user.
                }
            }
        } catch (Exception ignored) { // Malformed or wrong-signature JWT: treat as unauthenticated; downstream returns 401 if needed.
            SecurityContextHolder.clearContext(); // Drops any partial state on parse failures.
        }
        filterChain.doFilter(request, response); // Always proceed; authorization rules decide 401/403.
    }
}
