package org.example.oauth2.user.controller;

import lombok.RequiredArgsConstructor;

import org.example.oauth2.user.dto.UserRequestDto;
import org.example.oauth2.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController
{
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<String> userRegister(@RequestBody UserRequestDto.RegisterRequest registerRequest)
    {
        userService.userRegister(registerRequest);
        return ResponseEntity.ok("success");
    }

}
