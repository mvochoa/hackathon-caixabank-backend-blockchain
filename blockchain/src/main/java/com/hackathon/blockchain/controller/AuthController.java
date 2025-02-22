package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.dto.auth.LoginUserDto;
import com.hackathon.blockchain.dto.auth.RegisterUserDto;
import com.hackathon.blockchain.dto.auth.SessionDto;
import com.hackathon.blockchain.dto.auth.SessionUserDto;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(userService.register(registerUserDto))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessageDto> login(@Valid @RequestBody LoginUserDto loginUserDto,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        String message = userService.login(loginUserDto);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(message)
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/check-session")
    public ResponseEntity<SessionDto> checkSession(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                SessionDto.builder()
                        .user(SessionUserDto.builder()
                                .username(user.getUsername())
                                .build())
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessageDto> logout(HttpServletRequest request,
                                                     HttpServletResponse response) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message("Logged out successfully")
                        .build(),
                HttpStatus.OK);
    }
}