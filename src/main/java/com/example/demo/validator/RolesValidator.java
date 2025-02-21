package com.example.demo.validator;

import com.example.demo.entity.Member;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class RolesValidator implements ConstraintValidator<ValidRoles, Set<Member.MemberRole>> {

    @Override
    public boolean isValid(Set<Member.MemberRole> roles, ConstraintValidatorContext context) {
        // roles 가 null 이면 기본값 허용 (DTO 에서 기본값 제공)
        if (roles == null) {
            return true;
        }

        // 허용된 값 (ROLE_USER 또는 ROLE_ADMIN)만 존재해야 함
        return !(roles.contains(Member.MemberRole.ROLE_USER) || roles.contains(Member.MemberRole.ROLE_ADMIN));
    }
}
