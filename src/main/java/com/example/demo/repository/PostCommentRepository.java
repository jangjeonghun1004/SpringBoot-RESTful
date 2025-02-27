package com.example.demo.repository;

import com.example.demo.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * 댓글 데이터 접근 레포지토리
 * <p>
 * 특정 게시글에 속한 댓글 목록을 조회하는 기능을 제공합니다.
 */
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    /**
     * 게시글 ID를 기준으로 댓글 목록을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 해당 게시글에 속한 댓글 목록
     */
    List<PostComment> findByPostId(Long postId);

    /**
     * 게시글 ID를 기준으로 댓글 목록을 삭제합니다.
     *
     * @param postId 게시글 ID
     */
    @Modifying
    @Query("delete from PostComment pc where pc.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
