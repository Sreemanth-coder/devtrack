package com.devtrack.service;

import com.devtrack.dto.auth.AuthResponse;
import com.devtrack.dto.auth.LoginRequest;
import com.devtrack.dto.auth.RegisterRequest;
import com.devtrack.dto.user.UserResponse;
import com.devtrack.entity.User;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.InvalidCredentialsException;
import com.devtrack.repository.UserRepository;
import com.devtrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateAccessToken(saved, saved.getId());

        return AuthResponse.of(token, jwtService.getAccessTokenExpirationMs(), UserResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateAccessToken(user, user.getId());
        return AuthResponse.of(token, jwtService.getAccessTokenExpirationMs(), UserResponse.from(user));
    }
}
