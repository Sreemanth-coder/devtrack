package com.devtrack.controller;

import com.devtrack.entity.User;
import com.devtrack.repository.ProblemRepository;
import com.devtrack.repository.UserRepository;
import com.devtrack.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        problemRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .name("Jane Dev")
                .email("jane@example.com")
                .passwordHash(passwordEncoder.encode("SuperSecret123"))
                .build();
        user = userRepository.save(user);
        token = jwtService.generateAccessToken(user, user.getId());
    }

    @Test
    void create_withMissingTitle_returnsValidationError() throws Exception {
        String body = """
                {
                  "topic": "Arrays",
                  "difficulty": "EASY",
                  "status": "TODO"
                }
                """;

        mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title')]").exists());
    }

    @Test
    void create_withInvalidDifficultyEnum_returnsBadRequestNotServerError() throws Exception {
        String body = """
                {
                  "title": "Two Sum",
                  "topic": "Arrays",
                  "difficulty": "SUPER_HARD",
                  "status": "TODO"
                }
                """;

        mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_withInvalidDifficultyQueryParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/problems").param("difficulty", "NOT_A_DIFFICULTY")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_thenList_returnsCreatedProblem() throws Exception {
        String body = """
                {
                  "title": "Two Sum",
                  "topic": "Arrays",
                  "difficulty": "EASY",
                  "platform": "LeetCode",
                  "programmingLanguage": "JAVA",
                  "problemUrl": "https://leetcode.com/problems/two-sum",
                  "status": "TODO",
                  "notes": "use a hashmap"
                }
                """;

        mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Two Sum"));

        mockMvc.perform(get("/api/problems")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Two Sum"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isUnauthorized());
    }
}
