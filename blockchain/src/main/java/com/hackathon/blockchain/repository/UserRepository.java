package com.hackathon.blockchain.repository;

import com.hackathon.blockchain.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    public User save(User user) {
        return user;
    }
}
