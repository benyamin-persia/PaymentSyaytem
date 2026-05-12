package com.bank;

import com.bank.model.AppUser;
import com.bank.repository.AppUserRepository;
import com.bank.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationFlowMockMvcTest {

    @Autowired
    private MockMvc mockMvc;                                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @Autowired
    private AppUserRepository appUserRepository;                                                              // Concept: Field | Why: stores persistent state required by this type.

    @Autowired
    private PasswordEncoder passwordEncoder;                                                                  // Concept: Field | Why: stores credential data for authentication decisions.

    @MockitoBean
    private EmailService emailService;                                                                        // Concept: Note | Why: mock email sender to keep this test deterministic and offline-safe.

    @Test
    void register_persistsUsernameEmailAndEncodedPassword() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);                      // Concept: Note | Why: random suffix keeps each run independent when the same username was left in the DB from a prior run.
        String username = "newuser_" + suffix;                                                                // Concept: Note | Why: unique username avoids collisions across repeated local test runs.
        String email = "newuser_" + suffix + "@example.com";                                                    // Concept: Note | Why: unique email validates the new dedicated email field flow.
        String rawPassword = "MySecurePass123!";                                                              // Concept: Note | Why: raw input is used to verify encoder output, not stored directly.

        mockMvc.perform(post("/register")
                        .with(csrf())                                                                         // Concept: Note | Why: cSRF token is required because registration POST is protected by CSRF.
                        .param("username", username)
                        .param("email", email)
                        .param("password", rawPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        Optional<AppUser> savedUserOptional = appUserRepository.findByUsername(username);                     // Concept: Note | Why: query by username validates persistence key path.
        assertThat(savedUserOptional).isPresent();

        AppUser savedUser = savedUserOptional.get();
        assertThat(savedUser.getUsername()).isEqualTo(username);                                              // Concept: Note | Why: confirms username is stored from form submission.
        assertThat(savedUser.getEmail()).isEqualTo(email);                                                    // Concept: Note | Why: confirms email is stored as a separate dedicated field.
        assertThat(savedUser.getPassword()).isNotEqualTo(rawPassword);                                        // Concept: Note | Why: confirms password is not persisted in plain text.
        assertThat(passwordEncoder.matches(rawPassword, savedUser.getPassword())).isTrue();                   // Concept: Note | Why: confirms stored hash matches the submitted password.
    }
}
