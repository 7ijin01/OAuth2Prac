package org.example.oauth2.user.service;

import lombok.RequiredArgsConstructor;

import org.example.oauth2.user.dto.UserRequestDto;
import org.example.oauth2.user.entity.UserEntity;
import org.example.oauth2.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void userRegister(UserRequestDto.RegisterRequest registerRequest)
    {
        UserEntity user = userRepository.findByEmail(registerRequest.getEmail());
        if(user==null)
        {
            UserEntity userEntity =new UserEntity();
            userEntity.setUsername(registerRequest.getUsername());
            userEntity.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
            userEntity.setEmail(registerRequest.getEmail());
            userEntity.setRole("ROLE_USER");
            userRepository.save(userEntity);
        }

    }

}
