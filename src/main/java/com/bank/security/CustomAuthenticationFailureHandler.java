package com.bank.security;

import com.bank.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final RegistrationService registrationService;                                                    // Concept: Field | Why: stores persistent state required by this type.

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        if (username != null && !username.isBlank()) {                                                        // Concept: Validation | Why: blocks invalid input before business logic executes.
            registrationService.onAuthenticationFailure(username);
        }
        response.sendRedirect("/login?error");
    }
}
