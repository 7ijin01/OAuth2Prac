package org.example.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.oauth2.jwt.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            ObjectMapper objectMapper=new ObjectMapper();
            Map<String, String> credentials=objectMapper.readValue(request.getInputStream(),Map.class);

            String email=credentials.get("email");

            String password= credentials.get("password");

            UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(email,password,null);

            return authenticationManager.authenticate(token);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보
        CustomUserDetails customUserDetails= (CustomUserDetails)authentication.getPrincipal();
        String username = customUserDetails.getUsername();



        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        //토큰 생성
        String access = jwtUtil.createJwt("Authorization", username,role, 600000L);
        String refresh = jwtUtil.createJwt("Refresh",username , role, 86400000L);


        response.setHeader("Authorization", access);
        response.addCookie(createCookie("Refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
    //로그인 경로 변경시 사용
//    @Override
//    public void setFilterProcessesUrl(String filterProcessesUrl) {
//        super.setFilterProcessesUrl(filterProcessesUrl);
//        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(filterProcessesUrl));
//    }
}
