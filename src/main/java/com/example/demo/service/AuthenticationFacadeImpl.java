package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.provider.MessageProvider;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AuthenticationFacade 인터페이스의 구현체
 * <p>
 * SecurityContextHolder에서 인증 정보를 가져와 현재 인증된 사용자를 반환합니다.
 * 인증되지 않았거나 익명 사용자일 경우, null을 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    private final MemberRepository memberRepository;

    /**
     * 현재 인증된 사용자의 Member 엔티티를 반환합니다.
     * <p>
     * 인증 정보가 없거나 익명 사용자일 경우 null을 반환합니다.
     * 단, 인증 정보가 존재하지만 해당 사용자를 찾을 수 없는 경우 ResourceNotFoundException을 발생시킵니다.
     *
     * @return 현재 인증된 Member 또는 null
     * @throws ResourceNotFoundException 인증 정보는 있으나 사용자를 찾을 수 없는 경우
     */
    @Override
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없거나 익명 사용자이면 null 반환
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return memberRepository.findMemberByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException")
                                + " email: " + authentication.getName()));
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     * <p>
     * 인증된 사용자가 없을 경우 0을 반환합니다.
     *
     * @return 현재 사용자의 ID 또는 0
     */
    @Override
    public Long getCurrentMemberId() {
        Member currentMember = getCurrentMember();
        return currentMember != null ? currentMember.getId() : 0;
    }
}
