package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 데이터 접근 레포지토리
 * <p>
 * 회원 정보를 검색, 저장 및 존재 여부를 확인하는 기능을 제공합니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일을 기반으로 회원 정보를 조회합니다.
     *
     * @param email 회원 이메일
     * @return 회원 정보 (Optional)
     */
    Optional<Member> findMemberByEmail(String email);

    /**
     * 해당 이메일이 존재하는지 확인합니다.
     *
     * @param email 회원 이메일
     * @return 이메일 존재 여부
     */
    boolean existsMemberByEmail(String email);
}
