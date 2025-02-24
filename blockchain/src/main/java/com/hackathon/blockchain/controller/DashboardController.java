package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getUserDashboard(@AuthenticationPrincipal User user) {
        if (user == null) {
            return new ResponseEntity<>("You are not authenticated", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Welcome to your dashboard, " + user.getUsername() + "!\n" +
                "Your registered email is: " + user.getEmail(), HttpStatus.OK);
    }
}