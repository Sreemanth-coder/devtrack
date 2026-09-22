package com.devtrack.dto.dsa;

import com.devtrack.entity.Difficulty;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProblemRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @NotBlank(message = "Topic is required")
        @Size(max = 100, message = "Topic must be at most 100 characters")
        String topic,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @Size(max = 100, message = "Platform must be at most 100 characters")
        String platform,

        ProgrammingLanguage programmingLanguage,

        @Size(max = 500, message = "Problem URL must be at most 500 characters")
        String problemUrl,

        @NotNull(message = "Status is required")
        ProblemStatus status,

        /**
         * Only meaningful when status is SOLVED. If status is SOLVED and this
         * is omitted, the service defaults it to today's date.
         */
        LocalDate solvedAt,

        @Size(max = 2000, message = "Notes must be at most 2000 characters")
        String notes
) {
}
