package com.devtrack.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 120, message = "Name must be at most 120 characters")
        String name,

        @Size(max = 1000, message = "Bio must be at most 1000 characters")
        String bio,

        @Size(max = 500, message = "Profile image URL must be at most 500 characters")
        String profileImageUrl,

        @Size(max = 120, message = "GitHub username must be at most 120 characters")
        String githubUsername
) {
}
