package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 게시글 데이터 접근 레포지토리.
 * JpaRepository를 상속하여 기본적인 CRUD 메서드를 제공받습니다.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * 게시글 제목에 특정 키워드가 포함된 게시글을 검색합니다.
     *
     * @param title 키워드
     * @param pageable 페이징 및 정렬 정보
     * @return 검색된 게시글의 페이지
     */
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}