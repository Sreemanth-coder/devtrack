package com.devtrack.service;

import com.devtrack.dto.skill.SkillRequest;
import com.devtrack.dto.skill.SkillResponse;
import com.devtrack.entity.Skill;
import com.devtrack.entity.SkillCategory;
import com.devtrack.entity.User;
import com.devtrack.exception.DuplicateResourceException;
import com.devtrack.exception.ResourceNotFoundException;
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
class SkillServiceTest {

    @Autowired
    private SkillService skillService;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
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

    @Test
    void list_includesPredefinedSkillsForEveryUser() {
        skillRepository.save(Skill.builder().name("Java").category(SkillCategory.LANGUAGE).custom(false).build());

        List<SkillResponse> visibleToOwner = skillService.list();
        loginAs(otherUser);
        List<SkillResponse> visibleToOther = skillService.list();

        assertThat(visibleToOwner).extracting("name").contains("Java");
        assertThat(visibleToOther).extracting("name").contains("Java");
    }

    @Test
    void list_excludesOtherUsersCustomSkills() {
        skillService.createCustom(new SkillRequest("My Internal Framework", SkillCategory.FRAMEWORK));

        loginAs(otherUser);
        List<SkillResponse> visibleToOther = skillService.list();

        assertThat(visibleToOther).extracting("name").doesNotContain("My Internal Framework");
    }

    @Test
    void createCustom_withDuplicateName_throwsConflict() {
        skillRepository.save(Skill.builder().name("Java").category(SkillCategory.LANGUAGE).custom(false).build());

        assertThatThrownBy(() -> skillService.createCustom(new SkillRequest("java", SkillCategory.LANGUAGE)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_anotherUsersCustomSkill_throwsNotFound() {
        SkillResponse created = skillService.createCustom(new SkillRequest("My Skill", SkillCategory.TOOL));

        loginAs(otherUser);

        assertThatThrownBy(() -> skillService.update(created.id(), new SkillRequest("Renamed", SkillCategory.TOOL)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_anotherUsersCustomSkill_throwsNotFound() {
        SkillResponse created = skillService.createCustom(new SkillRequest("My Skill", SkillCategory.TOOL));

        loginAs(otherUser);

        assertThatThrownBy(() -> skillService.delete(created.id()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
