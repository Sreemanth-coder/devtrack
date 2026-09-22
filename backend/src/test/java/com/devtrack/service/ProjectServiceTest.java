package com.devtrack.service;

import com.devtrack.dto.project.AttachSkillsRequest;
import com.devtrack.dto.project.ProjectRequest;
import com.devtrack.dto.project.ProjectResponse;
import com.devtrack.entity.ProjectStatus;
import com.devtrack.entity.Skill;
import com.devtrack.entity.SkillCategory;
import com.devtrack.entity.User;
import com.devtrack.exception.ResourceNotFoundException;
import com.devtrack.repository.ProjectRepository;
import com.devtrack.repository.SkillRepository;
import com.devtrack.repository.UserRepository;
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
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        skillRepository.deleteAll();
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
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private ProjectRequest sampleRequest() {
        return new ProjectRequest(
                "DevTrack", "A developer career tracker", ProjectStatus.IN_PROGRESS,
                "https://github.com/me/devtrack", null, null,
                "Auth, DSA tracker, skills", "Designing skill strength formula",
                "Learned Spring Security 7 changes", "Full-stack career tracker built with Spring Boot + React",
                "Be ready to explain the skill strength formula");
    }

    @Test
    void create_persistsProjectForCurrentUser() {
        ProjectResponse response = projectService.create(sampleRequest());

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("DevTrack");

        var stored = projectRepository.findById(response.id()).orElseThrow();
        assertThat(stored.getUser().getId()).isEqualTo(owner.getId());
    }

    @Test
    void getById_forAnotherUsersProject_throwsNotFound() {
        ProjectResponse created = projectService.create(sampleRequest());

        loginAs(otherUser);

        assertThatThrownBy(() -> projectService.getById(created.id()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_anotherUsersProject_throwsNotFound() {
        ProjectResponse created = projectService.create(sampleRequest());

        loginAs(otherUser);

        assertThatThrownBy(() -> projectService.update(created.id(), sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesProject() {
        ProjectResponse created = projectService.create(sampleRequest());

        projectService.delete(created.id());

        assertThat(projectRepository.findById(created.id())).isEmpty();
    }

    @Test
    void list_onlyReturnsCurrentUsersProjects() {
        projectService.create(sampleRequest());

        loginAs(otherUser);
        projectService.create(sampleRequest());

        loginAs(owner);
        List<ProjectResponse> projects = projectService.list();

        assertThat(projects).hasSize(1);
    }

    @Test
    void attachAndRemoveSkills_updatesProjectSkillSet() {
        Skill java = skillRepository.save(Skill.builder().name("Java").category(SkillCategory.LANGUAGE).build());
        Skill spring = skillRepository.save(Skill.builder().name("Spring Boot").category(SkillCategory.FRAMEWORK).build());

        ProjectResponse created = projectService.create(sampleRequest());

        ProjectResponse withSkills = projectService.attachSkills(
                created.id(), new AttachSkillsRequest(List.of(java.getId(), spring.getId())));

        assertThat(withSkills.skills()).hasSize(2);
        assertThat(withSkills.skills()).extracting("name").containsExactlyInAnyOrder("Java", "Spring Boot");

        ProjectResponse afterRemoval = projectService.removeSkill(created.id(), java.getId());

        assertThat(afterRemoval.skills()).hasSize(1);
        assertThat(afterRemoval.skills().get(0).name()).isEqualTo("Spring Boot");
    }
}
