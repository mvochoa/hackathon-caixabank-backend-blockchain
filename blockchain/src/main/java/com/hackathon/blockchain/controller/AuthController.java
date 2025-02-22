package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.auth.LoginUserDto;
import com.hackathon.blockchain.dto.auth.RegisterUserDto;
import com.hackathon.blockchain.dto.auth.SessionDto;
import com.hackathon.blockchain.dto.auth.SessionUserDto;
import com.hackathon.blockchain.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(userService.register(registerUserDto))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessageDto> login(HttpSession session, @Valid @RequestBody LoginUserDto loginUserDto) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(userService.login(session, loginUserDto))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/check-session")
    public ResponseEntity<SessionDto> checkSession(HttpSession session) {
        return new ResponseEntity<>(
                SessionDto.builder()
                        .user(SessionUserDto.builder()
                                .username(userService.checkSession(session))
                                .build())
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessageDto> logout(HttpSession session) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(userService.logout(session))
                        .build(),
                HttpStatus.OK);
    }
}