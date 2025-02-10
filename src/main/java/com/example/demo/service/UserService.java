package com.example.demo.service;

import com.example.demo.dto.sign.up.SignUpRequest;
import com.example.demo.dto.user.UserDTO;
import com.example.demo.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    UserDTO save(SignUpRequest signUpRequest);
    boolean existsByEmail(String email);
    Optional<User> authenticate(String email, String rawPassword);
}
