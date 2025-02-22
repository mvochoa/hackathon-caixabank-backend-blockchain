package com.hackathon.blockchain.service;

import com.hackathon.blockchain.dto.auth.LoginUserDto;
import com.hackathon.blockchain.dto.auth.RegisterUserDto;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterUserDto payload) {
        System.out.printf("Intentando guardar usuario: %s - %s\n", payload.getUsername(), payload.getEmail());

        if (userRepository.findByUsername(payload.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Username already exists");
        }

        User savedUser = userRepository.save(User.builder()
                .username(payload.getUsername())
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .build());
        System.out.println("Usuario guardado con ID: " + savedUser.getId());

        return "User registered and logged in successfully";
    }

    public String login(HttpSession session, LoginUserDto loginUserDto) {
        Optional<User> user = userRepository.findByUsername(loginUserDto.getUsername());
        if (user.isEmpty() || !passwordEncoder.matches(loginUserDto.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "❌ Invalid credentials");
        }

        session.setAttribute("username", loginUserDto.getUsername());
        return "Login successful";
    }

    public String checkSession(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "❌ Invalid credentials");
        }

        return username;
    }

    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully";
    }
}