package com.devtrack.service;

import com.devtrack.dto.auth.LoginRequest;
import com.devtrack.dto.auth.RegisterRequest;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.InvalidCredentialsException;
import com.devtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private static final String EMAIL = "jane.dev@example.com";
    private static final String PASSWORD = "SuperSecret123";

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_createsUserAndReturnsTokenWithoutPasswordHash() {
        var request = new RegisterRequest("Jane Dev", EMAIL, PASSWORD);

        var response = authService.register(request);

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.user().email()).isEqualTo(EMAIL.toLowerCase());
        assertThat(response.user().name()).isEqualTo("Jane Dev");

        // Ensure the stored password is hashed, never plaintext.
        var stored = userRepository.findByEmailIgnoreCase(EMAIL).orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(stored.getPassword()).startsWith("$2"); // bcrypt hash prefix
    }

    @Test
    void register_withDuplicateEmail_throwsConflict() {
        authService.register(new RegisterRequest("Jane Dev", EMAIL, PASSWORD));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("Someone Else", EMAIL, "OtherPass1")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void login_withCorrectCredentials_returnsToken() {
        authService.register(new RegisterRequest("Jane Dev", EMAIL, PASSWORD));

        var response = authService.login(new LoginRequest(EMAIL, PASSWORD));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.user().email()).isEqualTo(EMAIL.toLowerCase());
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentials() {
        authService.register(new RegisterRequest("Jane Dev", EMAIL, PASSWORD));

        assertThatThrownBy(() -> authService.login(new LoginRequest(EMAIL, "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_withUnknownEmail_throwsInvalidCredentials() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("nobody@example.com", PASSWORD)))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
