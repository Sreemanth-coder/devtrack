package com.devtrack.dto.project;

import com.devtrack.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProjectRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters")
        String name,

        @Size(max = 3000, message = "Description must be at most 3000 characters")
        String description,

        @NotNull(message = "Status is required")
        ProjectStatus status,

        @Size(max = 500, message = "GitHub URL must be at most 500 characters")
        @Pattern(regexp = "^$|^https?://.+", message = "GitHub URL must be a valid http(s) URL")
        String githubUrl,

        @Size(max = 500, message = "Live URL must be at most 500 characters")
        @Pattern(regexp = "^$|^https?://.+", message = "Live URL must be a valid http(s) URL")
        String liveUrl,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        @Pattern(regexp = "^$|^https?://.+", message = "Image URL must be a valid http(s) URL")
        String imageUrl,

        @Size(max = 3000, message = "Key features must be at most 3000 characters")
        String keyFeatures,

        @Size(max = 3000, message = "Challenges must be at most 3000 characters")
        String challenges,

        @Size(max = 3000, message = "Learnings must be at most 3000 characters")
        String learnings,

        @Size(max = 1000, message = "Resume description must be at most 1000 characters")
        String resumeDescription,

        @Size(max = 3000, message = "Interview notes must be at most 3000 characters")
        String interviewNotes
) {
}
