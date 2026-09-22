package com.devtrack.controller;

import com.devtrack.dto.user.UpdateProfileRequest;
import com.devtrack.dto.user.UserResponse;
import com.devtrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
