package com.tellem.service;

import com.tellem.dto.OAuth2UserInfoDto;
import com.tellem.dto.UserPrincipal;
import com.tellem.model.User;
import com.tellem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;  // Adăugăm dependența pentru JwtService

    @Override
    @SneakyThrows
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        log.trace("Load user {}", oAuth2UserRequest);
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId(); // Get registrationId
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        return processOAuth2User(registrationId, oAuth2UserRequest, oAuth2User);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private OAuth2User processOAuth2User(String registrationId, OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String userId = getSocialId(oAuth2User, registrationId);
        OAuth2UserInfoDto userInfoDto = OAuth2UserInfoDto
                .builder()
                .name(oAuth2User.getAttributes().get("name").toString())
                .id(userId)
                .email(oAuth2User.getAttributes().get("email").toString())
                .picture(oAuth2User.getAttributes().get("picture").toString())
                .build();

        log.trace("User info is {}", userInfoDto);
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(userInfoDto.getEmail()));
        log.trace("User is {}", userOptional);
        User user = userOptional
                .map(existingUser -> updateExistingUser(existingUser, userInfoDto))
                .orElseGet(() -> registerNewUser(oAuth2UserRequest, userInfoDto));

        String jwt = jwtService.generateToken(user.getUsername());  // Generate JWT
        System.out.println("JWT: " + jwt);
        System.out.println(oAuth2User.getAttributes());

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfoDto userInfoDto) {
        User user = new User();
        user.setProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        user.setProviderId(userInfoDto.getId());
        user.setName(userInfoDto.getName());
        user.setUsername(userInfoDto.getEmail());
        user.setPicture(userInfoDto.getPicture());
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfoDto userInfoDto) {
        existingUser.setName(userInfoDto.getName());
        existingUser.setPicture(userInfoDto.getPicture());
        return userRepository.save(existingUser);
    }

    private String getSocialId(OAuth2User oAuth2User, String provider) {
        if (provider.equals("facebook")) {
            return (String) oAuth2User.getAttributes().get("id");
        } else if (provider.equals("google") || provider.equals("apple")) {
            String sub = (String) oAuth2User.getAttributes().get("sub");
            if (sub != null) {
                return sub;
            } else {
                return (String) oAuth2User.getAttributes().get("id"); // Fallback to "id" if "sub" is null
            }
        } else {
            throw new IllegalArgumentException("Unsupported Provider");
        }
    }

}