package com.example.demo.dto.sign.in;

import com.example.demo.validator.ValidEmail;
import com.example.demo.validator.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    @ValidEmail
    private String email;

    @ValidPassword()
    private String password;
}
