package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.provider.MessageProvider;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AuthenticationFacade 인터페이스의 구현체
 * <p>
 * SecurityContextHolder에서 인증 정보를 가져와 현재 인증된 사용자를 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    private final MemberRepository memberRepository;

    @Override
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(MessageProvider.getMessage("common.validation.noAuthenticated"));
        }
        return memberRepository.findMemberByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " email: " + authentication.getName()));
    }

    @Override
    public Long getCurrentMemberId() {
        return this.getCurrentMember().getId();
    }

}
