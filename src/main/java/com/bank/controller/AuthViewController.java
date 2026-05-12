package com.bank.controller;

import com.bank.dto.RegistrationRequest;
import com.bank.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final RegistrationService registrationService;                                                    // Concept: Field | Why: stores persistent state required by this type.

    @GetMapping("/login")
    public String login() {                                                                                   // Concept: Method | Why: encapsulates login behavior for this component boundary.
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {                                                                     // Concept: Method | Why: encapsulates register behavior for this component boundary.
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegistrationRequest registrationRequest,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            registrationService.register(registrationRequest);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {                                               // Concept: Method | Why: encapsulates verify behavior for this component boundary.
        boolean verified = registrationService.verifyAccount(token);
        return verified ? "redirect:/login?verified" : "redirect:/login?invalidToken";
    }
}
