package com.devtrack.service;

import com.devtrack.dto.dsa.ProblemRequest;
import com.devtrack.entity.Difficulty;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import com.devtrack.entity.User;
import com.devtrack.repository.DSAActivityRepository;
import com.devtrack.repository.ProblemRepository;
import com.devtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DsaStatsServiceTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private DsaStatsService dsaStatsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private DSAActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .name("Jane Dev")
                .email("jane@example.com")
                .passwordHash(passwordEncoder.encode("SuperSecret123"))
                .build();
        user = userRepository.save(user);

        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    void getStats_aggregatesCountsCorrectly() {
        problemService.create(new ProblemRequest("A", "Arrays", Difficulty.EASY, "LeetCode",
                ProgrammingLanguage.JAVA, null, ProblemStatus.SOLVED, LocalDate.now(), null));
        problemService.create(new ProblemRequest("B", "Arrays", Difficulty.MEDIUM, "LeetCode",
                ProgrammingLanguage.JAVA, null, ProblemStatus.TODO, null, null));
        problemService.create(new ProblemRequest("C", "Graphs", Difficulty.HARD, "LeetCode",
                ProgrammingLanguage.CPP, null, ProblemStatus.IN_PROGRESS, null, null));

        var stats = dsaStatsService.getStats();

        assertThat(stats.totalProblems()).isEqualTo(3);
        assertThat(stats.totalSolved()).isEqualTo(1);
        assertThat(stats.totalTodo()).isEqualTo(1);
        assertThat(stats.totalInProgress()).isEqualTo(1);
        assertThat(stats.difficultyBreakdown().easy()).isEqualTo(1);
        assertThat(stats.difficultyBreakdown().medium()).isEqualTo(1);
        assertThat(stats.difficultyBreakdown().hard()).isEqualTo(1);
        assertThat(stats.topicProgress()).hasSize(2);
        assertThat(stats.currentStreak()).isEqualTo(1);
        assertThat(stats.longestStreak()).isEqualTo(1);
    }
}
