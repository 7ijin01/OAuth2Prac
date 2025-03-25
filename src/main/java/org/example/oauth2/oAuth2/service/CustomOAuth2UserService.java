package org.example.oauth2.oAuth2.service;

import lombok.RequiredArgsConstructor;
import org.example.oauth2.oAuth2.dto.GoogleResponse;
import org.example.oauth2.oAuth2.dto.NaverResponse;
import org.example.oauth2.oAuth2.dto.OAuth2Response;
import org.example.oauth2.oAuth2.dto.user.CustomOAuth2User;
import org.example.oauth2.oAuth2.dto.user.UserDto;
import org.example.oauth2.user.entity.UserEntity;
import org.example.oauth2.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService
{
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //userRequest는 리소스 서버에서 발급받아짐

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        UserEntity existData = userRepository.findByEmail(oAuth2Response.getEmail());

        if (existData == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setUsername(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            UserDto userDTO = new UserDto();
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getName());

            userRepository.save(existData);

            UserDto userDTO = new UserDto();
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }
    }
}
