package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 사용자 엔티티(Entity)
 * <p>
 * Spring Security 의 {@link UserDetails}를 구현하여 사용자 인증 및 권한 관리를 담당합니다.
 * 주요 보안 고려 사항:
 * - 비밀번호는 반드시 해싱하여 저장해야 합니다.
 * - 로그 출력 시 비밀번호가 노출되지 않도록 {@code @ToString(exclude = {"password"})}을 적용했습니다.
 * </p>
 */
@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직접 객체 생성 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더 패턴을 통한 객체 생성 유도
@FieldDefaults(level = AccessLevel.PRIVATE) // 필드 기본 접근 제한자
@EqualsAndHashCode(of = "id") // ID를 기준으로 동등성 비교
@ToString(exclude = {"password"}) // 비밀번호 노출 방지
public class Member implements UserDetails {

    /**
     * 엔티티 고유 식별자 (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * 사용자 이메일 (로그인 ID)
     */
    @Column(unique = true, nullable = false, length = 100)
    String email;

    /**
     * 사용자 비밀번호 (해싱 저장 필수)
     */
    @Column(nullable = false)
    String password;

    /**
     * 사용자 생성일
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    /**
     * 사용자 수정일
     */
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    /**
     * 사용자 권한 정보
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    @Column(nullable = false) // 권한 정보가 null 이 되지 않도록 보장
    @Enumerated(EnumType.STRING)
    Set<MemberRole> roles;

    /**
     * 계정 활성화 여부
     */
    @Column(nullable = false)
    boolean enabled;

    /**
     * 계정 잠금 여부
     */
    @Column(nullable = false)
    boolean accountNonLocked;

    /**
     * 비밀번호 만료 여부
     */
    @Column(nullable = false)
    boolean credentialsNonExpired;

    /**
     * 계정 만료 여부
     */
    @Column(nullable = false)
    boolean accountNonExpired;

    /**
     * 사용자 역할(권한) 정의
     */
    public enum MemberRole {
        ROLE_USER, ROLE_ADMIN
    }

    /**
     * Spring Security 에서 사용자 권한 정보 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    /**
     * Spring Security 에서 사용자 이름(로그인 ID)으로 사용되는 이메일 반환
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * 계정 만료 여부 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    /**
     * 계정 잠금 여부 반환
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * 비밀번호 만료 여부 반환
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    /**
     * 계정 활성화 여부 반환
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * 비밀번호 변경 메서드 (유효성 검사 추가)
     */
    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        this.password = encodedPassword;
    }

    /**
     * 빌더 패턴을 적용한 생성자
     */
    @Builder
    public Member(String email, String password, Set<MemberRole> roles, boolean enabled, boolean accountNonLocked) {
        this.email = email;
        this.password = password;
        // roles 가 null 일 경우 기본 ROLE_USER 적용
        this.roles = (roles != null) ? Collections.unmodifiableSet(roles) : Set.of(MemberRole.ROLE_USER);
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
    }

}
