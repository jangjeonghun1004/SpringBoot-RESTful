package com.example.demo.service;

import com.example.demo.entity.Member;

/**
 * 현재 인증 정보를 제공하는 인터페이스
 */
public interface AuthenticationFacade {

    /**
     * 현재 인증된 Member 엔티티를 반환합니다.
     *
     * @return 현재 인증된 Member
     * @throws IllegalStateException 인증 정보가 없거나 사용자를 찾을 수 없을 경우 예외 발생
     */
    Member getCurrentMember();

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * @return 현재 사용자의 ID 또는 0
     */
    Long getCurrentMemberId();
}
