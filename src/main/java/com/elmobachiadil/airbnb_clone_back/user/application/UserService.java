package com.elmobachiadil.airbnb_clone_back.user.application;

import com.elmobachiadil.airbnb_clone_back.infrastructure.config.SecurityUtils;
import com.elmobachiadil.airbnb_clone_back.user.application.dto.ReadUserDTO;
import com.elmobachiadil.airbnb_clone_back.user.domain.User;
import com.elmobachiadil.airbnb_clone_back.user.mapper.UserMapper;
import com.elmobachiadil.airbnb_clone_back.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final String UPDATED_AT_KEY = "updated_at";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public ReadUserDTO getAuthenticatedUserFromSecurityContext() {
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = SecurityUtils.mapOauth2AttributesToUser(principal.getAttributes());
        return getByEmail(user.getEmail()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findOneByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    public void syncWithIdp(OAuth2User oAuth2User, boolean forceResync) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user = SecurityUtils.mapOauth2AttributesToUser(attributes);
        Optional<User> existingUserOpt = userRepository.findOneByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            Instant lastModifiedDate = existingUser.getLastModifiedDate();
            Instant idpModifiedDate = attributes.get(UPDATED_AT_KEY) instanceof Instant ?
                    (Instant) attributes.get(UPDATED_AT_KEY) : Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));

            if (idpModifiedDate.isAfter(lastModifiedDate) || forceResync) {
                updateUser(existingUser, user);
            }
        } else {
            // If the user doesn't exist, save the new user
            userRepository.saveAndFlush(user);
        }
    }

    private void updateUser(User existingUser, User newUserData) {
        existingUser.setFirstName(newUserData.getFirstName());
        existingUser.setLastName(newUserData.getLastName());
        existingUser.setImageUrl(newUserData.getImageUrl());
        existingUser.setAuthorities(newUserData.getAuthorities());
        existingUser.setLastModifiedDate(Instant.now());
        userRepository.saveAndFlush(existingUser);
    }

    public Optional<ReadUserDTO> getBYPublicId(UUID publicId) {
        Optional<User> oneByPublicId = userRepository.findOneByPublicId(publicId);
        return oneByPublicId.map(userMapper::readUserDTOToUser);
    }
}
