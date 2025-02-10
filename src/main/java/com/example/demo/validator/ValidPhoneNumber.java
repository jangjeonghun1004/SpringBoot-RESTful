package com.example.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "유효하지 않은 전화번호 형식입니다 (예: 010-1234-5678)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // 추가 옵션: 하이픈 허용 여부 (기본값 true)
    boolean allowHyphen() default true;
}