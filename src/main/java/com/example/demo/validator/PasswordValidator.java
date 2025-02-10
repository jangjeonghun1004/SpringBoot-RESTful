package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 20;
    // 정규표현식: 최소 8자, 영문과 숫자 조합이며 최대 MAX_LENGTH 까지 허용.
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // 초기화 로직이 필요할 경우 구현 (여기서는 별도 초기화 없음)
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // null 값은 검증 실패로 처리할 경우 (null 허용 시 true)
        if (password == null || password.isBlank()) {
            return false;
        }
        // 길이 검증과 정규표현식 매칭을 동시에 수행
        return pattern.matcher(password).matches();
    }
}

