package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 댓글(PostComment) 엔티티
 * <p>
 * 각 댓글은 특정 게시글(Post)에 속하며, 댓글 내용과 생성/수정 시간을 포함합니다.
 */
@Entity
@Table(name = "post_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostComment {

    /**
     * 댓글의 고유 ID (자동 생성)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * 댓글 내용 (빈 값 불가)
     */
    @Column(nullable = false)
    String content;

    /**
     * 댓글이 속한 게시글(Post)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Post post;

    /**
     * 댓글 작성자(Member)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Member member;

    /**
     * 댓글 생성 시간 (자동 설정)
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    /**
     * 댓글 수정 시간 (자동 갱신)
     */
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Builder
    public PostComment(String content, Post post, Member member) {
        this.content = content;
        this.post = post;
        this.member = member;
    }

}
