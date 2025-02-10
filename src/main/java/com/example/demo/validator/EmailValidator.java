package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    // 이메일 형식을 검증하는 정규 표현식
    // 사용자명: 영문 대소문자, 숫자, '+', '_', '.', '-' 사용 가능
    // 도메인: 영문 대소문자, 숫자, '.', '-' 사용 가능하며, 마지막에 반드시 최상위 도메인(TLD)이 있어야 함.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        // 이메일 값이 null이거나 공백인 경우 유효하지 않음
        if (email == null || email.isBlank()) {
            return false;
        }

        // 정규 표현식으로 이메일 형식 검증
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
