package com.example.demo.dto.sign.up;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponse {
    private Long id;
    private String email;
}
