package org.example.oauth2.jwt.security;


import lombok.RequiredArgsConstructor;
import org.example.oauth2.user.entity.UserEntity;
import org.example.oauth2.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity =userRepository.findByEmail(email);
        if(userEntity == null)
        {
            return null;
        }
        return new CustomUserDetails(userEntity);
    }
}
