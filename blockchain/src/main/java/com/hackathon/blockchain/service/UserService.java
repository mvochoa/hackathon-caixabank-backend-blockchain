package com.hackathon.blockchain.service;

import com.hackathon.blockchain.dto.auth.LoginUserDto;
import com.hackathon.blockchain.dto.auth.RegisterUserDto;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.hackathon.blockchain.model.Constants.responseInvalidCredential;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
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

    public String login(LoginUserDto loginUserDto) {
        Optional<User> user = userRepository.findByUsername(loginUserDto.getUsername());
        if (user.isEmpty() || !passwordEncoder.matches(loginUserDto.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, responseInvalidCredential.getMessage());
        }

        return "Login successful";
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}