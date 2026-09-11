package com.devtrack.service;

import com.devtrack.dto.dsa.ProblemRequest;
import com.devtrack.dto.dsa.ProblemResponse;
import com.devtrack.entity.Difficulty;
import com.devtrack.entity.Problem;
import com.devtrack.entity.ProblemStatus;
import com.devtrack.entity.ProgrammingLanguage;
import com.devtrack.entity.User;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.DSAActivityRepository;
import com.devtrack.repository.ProblemRepository;
import com.devtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private DsaActivityService dsaActivityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private DSAActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser("owner@example.com");
        otherUser = createUser("other@example.com");

        loginAs(owner);
    }

    private User createUser(String email) {
        User user = User.builder()
                .name("Test User")
                .email(email)
                .passwordHash(passwordEncoder.encode("SuperSecret123"))
                .build();
        return userRepository.save(user);
    }

    private void loginAs(User user) {
        UserDetails principal = user;
        var authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private ProblemRequest sampleRequest(ProblemStatus status, LocalDate solvedAt) {
        return new ProblemRequest(
                "Two Sum", "Arrays", Difficulty.EASY, "LeetCode", ProgrammingLanguage.JAVA,
                "https://leetcode.com/problems/two-sum", status, solvedAt, "use a hashmap");
    }

    @Test
    void create_persistsProblemForCurrentUser() {
        ProblemResponse response = problemService.create(sampleRequest(ProblemStatus.TODO, null));

        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Two Sum");
        assertThat(response.status()).isEqualTo(ProblemStatus.TODO);

        Problem stored = problemRepository.findById(response.id()).orElseThrow();
        assertThat(stored.getUser().getId()).isEqualTo(owner.getId());
    }

    @Test
    void create_withSolvedStatus_recordsActivityForToday() {
        problemService.create(sampleRequest(ProblemStatus.SOLVED, null));

        var activity = activityRepository.findByUserIdAndActivityDate(owner.getId(), LocalDate.now());
        assertThat(activity).isPresent();
        assertThat(activity.get().getSolvedCount()).isEqualTo(1);
    }

    @Test
    void getById_forAnotherUsersProblem_throwsNotFound() {
        ProblemResponse created = problemService.create(sampleRequest(ProblemStatus.TODO, null));

        loginAs(otherUser);

        assertThatThrownBy(() -> problemService.getById(created.id()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_movingToSolved_incrementsActivityAndMovingAwayDecrementsIt() {
        ProblemResponse created = problemService.create(sampleRequest(ProblemStatus.TODO, null));

        LocalDate solveDate = LocalDate.now();
        problemService.update(created.id(), sampleRequest(ProblemStatus.SOLVED, solveDate));

        var activityAfterSolve = activityRepository.findByUserIdAndActivityDate(owner.getId(), solveDate);
        assertThat(activityAfterSolve).isPresent();
        assertThat(activityAfterSolve.get().getSolvedCount()).isEqualTo(1);

        problemService.update(created.id(), sampleRequest(ProblemStatus.IN_PROGRESS, null));

        var activityAfterRevert = activityRepository.findByUserIdAndActivityDate(owner.getId(), solveDate);
        assertThat(activityAfterRevert).isEmpty(); // row deleted once count hits zero
    }

    @Test
    void update_anotherUsersProblem_throwsNotFound() {
        ProblemResponse created = problemService.create(sampleRequest(ProblemStatus.TODO, null));

        loginAs(otherUser);

        assertThatThrownBy(() -> problemService.update(created.id(), sampleRequest(ProblemStatus.SOLVED, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_solvedProblem_removesActivityCount() {
        LocalDate solveDate = LocalDate.now();
        ProblemResponse created = problemService.create(sampleRequest(ProblemStatus.SOLVED, solveDate));

        problemService.delete(created.id());

        assertThat(problemRepository.findById(created.id())).isEmpty();
        assertThat(activityRepository.findByUserIdAndActivityDate(owner.getId(), solveDate)).isEmpty();
    }

    @Test
    void search_filtersByStatusAndScopesToCurrentUser() {
        problemService.create(sampleRequest(ProblemStatus.TODO, null));
        problemService.create(sampleRequest(ProblemStatus.SOLVED, LocalDate.now()));

        loginAs(otherUser);
        problemService.create(sampleRequest(ProblemStatus.SOLVED, LocalDate.now()));

        loginAs(owner);
        var page = problemService.search(null, null, null, ProblemStatus.SOLVED, null,
                PageRequest.of(0, 10));

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content()).allSatisfy(p -> assertThat(p.status()).isEqualTo(ProblemStatus.SOLVED));
    }

    @Test
    void computeStreaks_consecutiveDays_yieldsCorrectCurrentAndLongest() {
        LocalDate today = LocalDate.now();
        dsaActivityService.recordSolved(owner.getId(), today.minusDays(2));
        dsaActivityService.recordSolved(owner.getId(), today.minusDays(1));
        dsaActivityService.recordSolved(owner.getId(), today);

        var result = dsaActivityService.computeStreaks(owner.getId());

        assertThat(result.currentStreak()).isEqualTo(3);
        assertThat(result.longestStreak()).isEqualTo(3);
    }

    @Test
    void computeStreaks_withGap_longestStreakIgnoresBreak() {
        LocalDate today = LocalDate.now();
        dsaActivityService.recordSolved(owner.getId(), today.minusDays(10));
        dsaActivityService.recordSolved(owner.getId(), today.minusDays(9));
        dsaActivityService.recordSolved(owner.getId(), today.minusDays(8));
        // gap
        dsaActivityService.recordSolved(owner.getId(), today);

        var result = dsaActivityService.computeStreaks(owner.getId());

        assertThat(result.longestStreak()).isEqualTo(3);
        assertThat(result.currentStreak()).isEqualTo(1);
    }
}
