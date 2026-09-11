package com.devtrack.service;

import com.devtrack.dto.user.UpdateProfileRequest;
import com.devtrack.dto.user.UserResponse;
import com.devtrack.entity.User;
import com.devtrack.repository.UserRepository;
import com.devtrack.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return UserResponse.from(currentUserProvider.getCurrentUser());
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = currentUserProvider.getCurrentUser();

        if (StringUtils.hasText(request.name())) {
            user.setName(request.name());
        }
        // bio/profileImageUrl/githubUsername are allowed to be cleared, so we
        // only skip the field entirely when it's absent from the request (null),
        // not when it's an explicit empty string.
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl());
        }
        if (request.githubUsername() != null) {
            user.setGithubUsername(request.githubUsername());
        }

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}
