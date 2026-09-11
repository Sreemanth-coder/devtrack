package com.devtrack.util;

import com.devtrack.entity.User;
import com.devtrack.exception.InvalidCredentialsException;
import com.devtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Single source of truth for "who is making this request".
 *
 * Every controller/service in every module (DSA, Skills, Projects,
 * Applications, GitHub) must resolve the current user through this
 * class rather than accepting a userId from the request body/params.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("No authenticated user found");
        }
        // The JwtAuthenticationFilter puts our User (a UserDetails) as the principal.
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        // Fallback: principal was a username string (e.g. in some test setups) - look up fresh.
        String email = authentication.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new InvalidCredentialsException("Authenticated user no longer exists"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
