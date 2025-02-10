package com.example.demo.dto.sign.up;

import com.example.demo.entity.User;
import com.example.demo.validator.ValidEmail;
import com.example.demo.validator.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequest {
    @ValidEmail
    private String email;

    @ValidPassword()
    private String password;

    private Set<User.UserRole> roles = Set.of(User.UserRole.ROLE_USER);
}
