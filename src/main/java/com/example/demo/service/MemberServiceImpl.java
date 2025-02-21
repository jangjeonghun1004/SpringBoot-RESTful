package com.example.demo.service;

import com.example.demo.dto.sign.up.SignUpRequest;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.entity.Member;
import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * 사용자 서비스 구현 클래스
 *
 * 이 클래스는 사용자 관련 비즈니스 로직을 처리하는 서비스 레이어로,
 * 회원 가입, 사용자 조회, 인증 등의 기능을 제공한다.
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;  // 사용자 저장소 (JPA Repository)
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 PasswordEncoder

    /**
     * 이메일을 기반으로 사용자 엔티티(User)를 조회한다.
     *
     * @param email 조회할 사용자의 이메일
     * @return 존재하는 경우 User 엔티티를 Optional로 감싸서 반환, 없으면 Optional.empty() 반환
     */
    @Override
    public Optional<Member> findMemberByEmail(String email) {
        return this.memberRepository.findMemberByEmail(email);
    }

    /**
     * 회원 가입 기능을 수행한다.
     *
     * - 이미 존재하는 이메일이면 예외를 발생시킨다.
     * - 비밀번호는 암호화되어 저장된다.
     * - 새로운 사용자가 성공적으로 저장되면, 해당 사용자의 정보를 DTO로 변환하여 반환한다.
     *
     * @param signUpRequest 회원 가입 요청 정보 (이메일, 비밀번호, 역할 등)
     * @return 저장된 사용자 정보를 UserDTO 객체로 변환하여 반환
     * @throws MemberAlreadyExistsException 동일한 이메일이 이미 존재하는 경우 발생
     */
    @Override
    @Transactional  // 데이터 일관성을 위해 트랜잭션 처리
    public MemberResponse saveMember(SignUpRequest signUpRequest) {
        // 1. 중복 이메일 체크 (이미 존재하는 경우 예외 발생)
        if (this.memberRepository.existsMemberByEmail(signUpRequest.getEmail())) {
            throw new MemberAlreadyExistsException("이미 존재하는 이메일입니다: " + signUpRequest.getEmail());
        }

        // 2. 사용자 객체 생성 및 비밀번호 암호화 후 저장
        Member savedMember = this.memberRepository.save(
                Member.builder()
                        .email(signUpRequest.getEmail())
                        .password(this.passwordEncoder.encode(signUpRequest.getPassword())) // 비밀번호 암호화 저장
                        .roles(Set.of(Member.MemberRole.ROLE_USER)) // 사용자 역할(Role) 설정
                        .accountNonLocked(true) // 계정 잠금 여부 (true = 잠금되지 않음)
                        .enabled(true) // 계정 활성화 여부 (true = 활성화됨)
                        .build()
        );

        // 3. 저장된 사용자 정보를 UserDTO 객체로 변환하여 반환
        return MemberResponse.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail()).build();
    }

    /**
     * 사용자를 인증(로그인)한다.
     *
     * - 이메일을 기반으로 사용자를 조회한다.
     * - 입력된 비밀번호와 저장된 암호화된 비밀번호를 비교하여 검증한다.
     * - 검증이 성공하면 사용자 엔티티를 Optional로 감싸서 반환한다.
     *
     * @param email 사용자 이메일
     * @param rawPassword 입력된 비밀번호 (암호화되지 않은 상태)
     * @return 인증 성공 시 User 엔티티를 Optional로 감싸서 반환, 실패 시 Optional.empty() 반환
     */
    public Optional<Member> authenticateMember(String email, String rawPassword) {
        return this.memberRepository.findMemberByEmail(email)
                .filter(member -> passwordEncoder.matches(rawPassword, member.getPassword()));
    }
}
