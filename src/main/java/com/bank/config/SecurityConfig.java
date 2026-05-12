package com.bank.config;

import com.bank.security.CustomAuthenticationFailureHandler;
import com.bank.security.CustomAuthenticationSuccessHandler;
import com.bank.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler; // Redirects browser users after successful form login.
    private final CustomAuthenticationFailureHandler failureHandler; // Surfaces form-login errors back to the login page.
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Parses Bearer JWT for REST before username-password filter runs.

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler,
                          CustomAuthenticationFailureHandler failureHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { // Exposes the built-in auth manager as a bean for AuthController.
        return authenticationConfiguration.getAuthenticationManager(); // Uses registered UserDetailsService + PasswordEncoder providers.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // REST clients cannot send CSRF tokens like browsers; APIs under /api/** skip CSRF checks.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/register", "/verify").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/customers/add").hasRole("ADMIN")
                        .requestMatchers("/customers").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // Login JSON endpoint must stay anonymous so tokens can be obtained.
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // OpenAPI + Swagger UI for manual JWT testing.
                        .requestMatchers("/api/**").authenticated() // Business REST APIs require session or valid JWT established by the filter.
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT runs first so /api/** sees an Authentication before access decisions.

        return http.build();
    }
}
