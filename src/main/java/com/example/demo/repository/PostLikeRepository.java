package com.example.demo.repository;

import com.example.demo.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 게시글 좋아요 데이터 접근 레포지토리
 * <p>
 * 특정 게시글에 대한 좋아요 정보를 조회하고, 사용자의 좋아요 여부를 확인하는 기능을 제공합니다.
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 게시글과 회원 ID를 기반으로 좋아요 정보를 조회합니다.
     *
     * @param postId   게시글 ID
     * @param memberId 회원 ID
     * @return 좋아요 정보 (Optional)
     */
    Optional<PostLike> findPostLikeByPostIdAndMemberId(Long postId, Long memberId);

    /**
     * 특정 회원이 특정 게시글을 좋아요 했는지 여부를 확인합니다.
     *
     * @param postId   게시글 ID
     * @param memberId 회원 ID
     * @return 좋아요 여부 (true: 좋아요 함, false: 좋아요 안 함)
     */
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
