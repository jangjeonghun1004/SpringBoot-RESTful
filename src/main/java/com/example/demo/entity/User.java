package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "user") // "user"는 예약어 가능성이 있어 "users"로 변경
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"password"}) // 비밀번호는 로그 출력에서 제외
@EqualsAndHashCode(of = "id") // ID 기반 동등성 비교
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // 반드시 해싱하여 저장해야 함

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // FK 생성 안 함)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @Column(nullable = false)
    private boolean enabled; // 계정 활성화 여부

    @Column(nullable = false)
    private boolean accountNonLocked; // 계정 잠김 여부

    @Column(nullable = false)
    private boolean credentialsNonExpired; // 비밀번호 만료 여부

    @Column(nullable = false)
    private boolean accountNonExpired; // 계정 만료 여부

    // 빌더 패턴 적용 (roles null 방지 및 기본값 설정)
    @Builder
    public User(String email, String password, Set<UserRole> roles, boolean enabled, boolean accountNonLocked) {
        this.email = email;
        this.password = password;
        this.roles = (roles != null) ? Set.copyOf(roles) : Set.of(UserRole.ROLE_USER);
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
    }

    // 사용자 역할 (권한)
    public enum UserRole {
        ROLE_USER, ROLE_ADMIN
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    // 비밀번호 변경 시 반드시 해싱 필요
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
