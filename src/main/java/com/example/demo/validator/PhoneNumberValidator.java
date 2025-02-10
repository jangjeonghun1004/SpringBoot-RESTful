package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator
        implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String HYPHEN_PATTERN = "^01[0-9]-(\\d{3,4})-(\\d{4})$";
    private static final String NO_HYPHEN_PATTERN = "^01[0-9]\\d{7,8}$";

    private boolean allowHyphen;

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.allowHyphen = constraintAnnotation.allowHyphen();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // @NotBlank와 함께 사용 가정
        }

        String regex = allowHyphen
                ? HYPHEN_PATTERN
                : NO_HYPHEN_PATTERN;

        return Pattern.matches(regex, value);
    }
}