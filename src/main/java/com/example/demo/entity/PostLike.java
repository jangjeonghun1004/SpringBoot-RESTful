package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 게시글 좋아요 엔티티
 * - 특정 회원이 특정 게시글에 좋아요를 눌렀을 때 저장됩니다.
 */
@Entity
@Table(name = "post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "member_id"})
})
@Getter
@ToString(exclude = "post")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직접 객체 생성 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더 패턴을 통한 객체 생성 유도
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLike {

    /** 기본 키 (자동 증가) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /** 게시글과의 연관관계 (다대일, LAZY 로딩) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Post post;

    /** 좋아요를 누른 회원 ID */
    @Column(name = "member_id", nullable = false, updatable = false)
    Long memberId;

    /** 좋아요 생성일 */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    /** 좋아요 수정일 */
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    /**
     * 빌더 패턴을 사용하여 안전한 객체 생성을 유도합니다.
     */
    @Builder
    public PostLike(Post post, Long memberId) {
        this.post = post;
        this.memberId = memberId;
    }
}
