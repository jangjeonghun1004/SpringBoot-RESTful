package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 자유 게시판 게시글 엔티티
 * <p>
 * 이 엔티티는 게시글의 제목, 내용, 좋아요 수, 생성일, 수정일을 관리하며,
 * 동시성 이슈를 고려하여 낙관적 락(@Version)을 적용하였습니다.
 * </p>
 */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 직접 객체 생성 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더 패턴을 통한 객체 생성 유도
@FieldDefaults(level = AccessLevel.PRIVATE) // 모든 필드를 private로 설정
@EqualsAndHashCode(of = "id")
@ToString(exclude = "member")
public class Post {

    /**
     * 게시글 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id = null;

    /**
     * 게시글 제목
     */
    @Column(nullable = false, length = 255)
    String title;

    /**
     * 게시글 내용 (대용량 데이터 저장을 위한 LOB)
     */
    @Lob
    @Column(nullable = false)
    String content;

    /**
     * 좋아요 수 (AtomicInteger를 사용하여 동시성 문제 해결)
     */
    @Column(nullable = false)
    AtomicInteger likeCount = new AtomicInteger(0);

    /**
     * 게시글 작성자 (ManyToOne 관계, LAZY 로딩)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Member member;

    /**
     * 게시글 생성일
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt = null;

    /**
     * 게시글 수정일
     */
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Builder
    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        likeCount.incrementAndGet();
    }

    /**
     * 좋아요 수 감소 (0 이하로 내려가지 않도록 제한)
     */
    public void decrementLikeCount() {
        likeCount.updateAndGet(count -> Math.max(count - 1, 0));
    }

    /**
     * 게식글 수정
     * - 게시글 제목
     * - 게시글 내용
     */
    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
