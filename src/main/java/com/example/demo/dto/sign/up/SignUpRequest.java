package com.example.demo.dto.sign.up;

import com.example.demo.entity.Member;
import com.example.demo.validator.ValidEmail;
import com.example.demo.validator.ValidPassword;
import com.example.demo.validator.ValidRoles;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequest {
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
