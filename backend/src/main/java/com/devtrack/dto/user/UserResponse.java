package com.devtrack.dto.user;

import com.devtrack.entity.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        String profileImageUrl,
        String bio,
        String githubUsername,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getBio(),
                user.getGithubUsername(),
                user.getCreatedAt()
        );
    }
}
