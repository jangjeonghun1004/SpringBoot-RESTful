package com.example.demo.service;

import com.example.demo.dto.sign.up.SignUpRequest;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.entity.Member;

import java.util.Optional;

/**
 * 회원 서비스 인터페이스
 * <p>
 * 회원 조회, 가입, 인증 관련 기능을 제공합니다.
 */
public interface MemberService {

    /**
     * 이메일을 기준으로 회원 정보를 조회합니다.
     *
     * @param email 회원 이메일
     * @return 회원 정보 (Optional)
     */
    Optional<Member> findMemberByEmail(String email);

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signUpRequest 회원가입 요청 데이터
     * @return 회원가입 성공 시 생성된 회원 정보
     */
    MemberResponse saveMember(SignUpRequest signUpRequest);

    /**
     * 이메일과 비밀번호를 이용하여 회원 인증을 수행합니다.
     *
     * @param email       회원 이메일
     * @param rawPassword 입력된 비밀번호 (암호화되지 않은 상태)
     * @return 인증된 회원 정보 (Optional)
     */
    Optional<Member> authenticateMember(String email, String rawPassword);
}
