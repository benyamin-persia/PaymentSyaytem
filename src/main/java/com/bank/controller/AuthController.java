package com.bank.controller;

import com.bank.dto.LoginRequest;
import com.bank.dto.LoginResponse;
import com.bank.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager; // Delegates credential check to DaoAuthenticationProvider + UserDetailsService.
    private final JwtService jwtService; // Issues signed JWT after successful authentication.

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) { // JSON body replaces form posts for SPA/mobile clients.
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())); // Triggers password hash verification.
            UserDetails principal = (UserDetails) authentication.getPrincipal(); // Standard post-auth principal type from UserDetailsService.
            String token = jwtService.generateToken(principal); // Embeds username/expiry for subsequent Bearer requests.
            return ResponseEntity.ok(new LoginResponse(token)); // 200 + token payload for client storage.
        } catch (BadCredentialsException ex) { // Wrong password or unknown user after provider checks.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // No body leak: generic 401 for failed login.
        }
    }
}
