package com.bank.security;

import com.bank.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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
        // Only count real wrong-password failures. Disabled/unverified accounts used to hit this path with the *correct* password and still incremented attempts → lockout before verify.
        if (username != null && !username.isBlank() && exception instanceof BadCredentialsException) { // BadCredentials means wrong password or unknown user, not "account disabled".
            registrationService.onAuthenticationFailure(username);
        }
        response.sendRedirect("/login?error");
    }
}
