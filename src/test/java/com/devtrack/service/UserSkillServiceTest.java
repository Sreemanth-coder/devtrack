package com.devtrack.service;

import com.devtrack.dto.project.AttachSkillsRequest;
import com.devtrack.dto.project.ProjectRequest;
import com.devtrack.dto.project.ProjectResponse;
import com.devtrack.dto.skill.UpdateUserSkillRequest;
import com.devtrack.dto.skill.UserSkillRequest;
import com.devtrack.dto.skill.UserSkillResponse;
import com.devtrack.entity.ProjectStatus;
import com.devtrack.entity.Skill;
import com.devtrack.entity.SkillCategory;
import com.devtrack.entity.User;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.ProjectRepository;
import com.devtrack.repository.SkillRepository;
import com.devtrack.repository.UserRepository;
import com.devtrack.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserSkillServiceTest {

    @Autowired
    private UserSkillService userSkillService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;
    private Skill javaSkill;

    @BeforeEach
    void setUp() {
        userSkillRepository.deleteAll();
        projectRepository.deleteAll();
        skillRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser("owner@example.com");
        otherUser = createUser("other@example.com");
        javaSkill = skillRepository.save(Skill.builder().name("Java").category(SkillCategory.LANGUAGE).build());

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
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private ProjectResponse createProject(ProjectStatus status) {
        ProjectRequest request = new ProjectRequest(
                "Project", "desc", status, null, null, null, null, null, null, null, null);
        return projectService.create(request);
    }

    @Test
    void create_withoutProjectsOrSelfAssessment_yieldsZeroStrength() {
        UserSkillResponse response = userSkillService.create(new UserSkillRequest(javaSkill.getId(), null));

        assertThat(response.strength()).isEqualTo(0);
        assertThat(response.evidence().projectCount()).isEqualTo(0);
    }

    @Test
    void create_duplicateSkillForSameUser_throwsConflict() {
        userSkillService.create(new UserSkillRequest(javaSkill.getId(), 50));

        assertThatThrownBy(() -> userSkillService.create(new UserSkillRequest(javaSkill.getId(), 60)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_withLinkedCompletedProjectAndSelfAssessment_matchesFormula() {
        ProjectResponse project = createProject(ProjectStatus.COMPLETED);
        projectService.attachSkills(project.id(), new AttachSkillsRequest(List.of(javaSkill.getId())));

        UserSkillResponse response = userSkillService.create(new UserSkillRequest(javaSkill.getId(), 80));

        // 1 completed project -> evidence = min(1.0/3.0,1)*100 = 33
        // strength = round(0.6*33 + 0.4*80) = round(19.8 + 32) = round(51.8) = 52
        assertThat(response.evidence().projectEvidenceScore()).isEqualTo(33);
        assertThat(response.strength()).isEqualTo(52);
        assertThat(response.evidence().projectCount()).isEqualTo(1);
        assertThat(response.evidence().formula()).contains("60%").contains("40%");
    }

    @Test
    void update_changesSelfAssessmentAndRecalculatesStrength() {
        UserSkillResponse created = userSkillService.create(new UserSkillRequest(javaSkill.getId(), null));
        assertThat(created.strength()).isEqualTo(0);

        UserSkillResponse updated = userSkillService.update(created.id(), new UpdateUserSkillRequest(50));

        // no projects -> strength == selfAssessment-weighted-only? Actually formula:
        // no projects -> evidence 0, self-assessment present -> strength = round(0.6*0 + 0.4*50) = 20
        assertThat(updated.strength()).isEqualTo(20);
    }

    @Test
    void update_anotherUsersUserSkill_throwsNotFound() {
        UserSkillResponse created = userSkillService.create(new UserSkillRequest(javaSkill.getId(), 50));

        loginAs(otherUser);

        assertThatThrownBy(() -> userSkillService.update(created.id(), new UpdateUserSkillRequest(90)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesUserSkill() {
        UserSkillResponse created = userSkillService.create(new UserSkillRequest(javaSkill.getId(), 50));

        userSkillService.delete(created.id());

        assertThat(userSkillRepository.findById(created.id())).isEmpty();
    }

    @Test
    void list_onlyReturnsCurrentUsersTrackedSkills() {
        userSkillService.create(new UserSkillRequest(javaSkill.getId(), 50));

        loginAs(otherUser);
        List<UserSkillResponse> otherUsersList = userSkillService.list();

        assertThat(otherUsersList).isEmpty();
    }
}
