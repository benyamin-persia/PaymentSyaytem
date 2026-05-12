package com.bank.service;

import com.bank.model.AppUser;
import com.bank.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;                                                        // Concept: Field | Why: stores persistent state required by this type.
    private final RegistrationService registrationService;                                                    // Concept: Field | Why: stores persistent state required by this type.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        registrationService.unlockIfLockTimeExpired(appUser);

        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
                .disabled(!appUser.isEnabled())
                .accountLocked(!appUser.isAccountNonLocked())
                .build();
    }
}
