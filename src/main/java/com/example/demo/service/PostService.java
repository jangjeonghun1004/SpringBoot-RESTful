package com.example.demo.service;

import com.example.demo.dto.post.PostRequest;
import com.example.demo.dto.post.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시글 서비스 인터페이스
 * <p>
 * 게시글 생성, 조회, 검색, 수정, 삭제 및 좋아요 기능을 제공합니다.
 */
public interface PostService {

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param postRequest 게시글 생성 요청 데이터
     * @return 생성된 게시글 정보
     */
    PostDto createPost(PostRequest postRequest);

    /**
     * 모든 게시글을 페이징하여 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 게시글 목록 (페이징 처리됨)
     */
    Page<PostDto> findAllPosts(Pageable pageable);

    /**
     * 제목을 기준으로 게시글을 검색합니다.
     *
     * @param title    검색할 게시글 제목 (부분 일치)
     * @param pageable 페이징 정보
     * @return 검색된 게시글 목록 (페이징 처리됨)
     */
    Page<PostDto> searchPostsByTitle(String title, Pageable pageable);

    /**
     * 특정 게시글을 ID로 조회합니다.
     *
     * @param id 조회할 게시글 ID
     * @return 조회된 게시글 정보
     */
    PostDto getPostById(Long id);

    /**
     * 기존 게시글을 수정합니다.
     *
     * @param id          수정할 게시글 ID
     * @param postRequest 수정할 데이터 (제목, 내용)
     * @return 수정된 게시글 정보
     */
    PostDto updatePost(Long id, PostRequest postRequest);

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param id 삭제할 게시글 ID
     */
    void deletePost(Long id);

    /**
     * 특정 게시글에 대한 사용자의 좋아요 상태를 토글합니다.
     * 이미 좋아요한 경우 취소하고, 그렇지 않으면 좋아요를 추가합니다.
     *
     * @param id 좋아요를 토글할 게시글 ID
     * @return 좋아요 상태가 변경된 게시글 정보
     */
    PostDto togglePostLike(Long id);
}
