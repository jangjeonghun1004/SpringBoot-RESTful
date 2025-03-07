package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.post.PostDto;
import com.example.demo.dto.post.PostDtoWithPaging;
import com.example.demo.dto.post.PostRequest;
import com.example.demo.service.PostCommentService;
import com.example.demo.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 자유 게시판 REST API 컨트롤러
 */
@Validated
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private static final int PAGE_SIZE = 10;
    private static final Sort DEFAULT_SORT = Sort.by("createdAt").descending();

    private final PostService postService;
    private final PostCommentService postCommentService;

    /**
     * 새로운 게시글 생성 API.
     * URL: POST /api/post
     *
     * @param postRequest 클라이언트로부터 전달받은 게시글 데이터
     * @return 생성된 게시글 정보와 HTTP 201 응답
     */
    @PostMapping
    public ResponseEntity<ApiResult<PostDto>> createPost(@RequestBody @Valid PostRequest postRequest) {
        PostDto createdPost = postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(createdPost));
    }

    /**
     * 전체 게시글 조회 API (페이징 지원).
     * URL: GET /api/post
     *
     * @param pageNumber 페이지 번호 (기본값: 0)
     * @return 게시글 페이지와 HTTP 200 응답
     */
    @GetMapping
    public ResponseEntity<ApiResult<PostDtoWithPaging>> findAllPosts(@RequestParam(defaultValue = "0") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, DEFAULT_SORT);
        Page<PostDto> postsPage = postService.findAllPosts(pageable);
        PostDtoWithPaging result = buildPostDtoWithPaging(postsPage);
        return ResponseEntity.ok(ApiResult.success(result));
    }

    /**
     * 게시글 검색 API (제목에 포함된 키워드를 기준으로 검색).
     * URL: GET /api/post/search?title=keyword
     *
     * @param title      검색할 키워드 (빈 값은 허용되지 않음)
     * @param pageNumber 페이지 번호 (기본값: 0)
     * @return 검색된 게시글 페이지와 HTTP 200 응답
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResult<PostDtoWithPaging>> searchPosts(
            @RequestParam @NotBlank(message = "{common.validation.notBlank}") String title,
            @RequestParam(defaultValue = "0") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, DEFAULT_SORT);
        Page<PostDto> postsPage = postService.searchPostsByTitle(title, pageable);
        PostDtoWithPaging result = buildPostDtoWithPaging(postsPage);
        return ResponseEntity.ok(ApiResult.success(result));
    }

    /**
     * 특정 게시글 조회 API.
     * URL: GET /api/post/{id}
     *
     * @param id 조회할 게시글 ID (양수여야 함)
     * @return 게시글 정보와 HTTP 200 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<PostDto>> getPostById(
            @PathVariable @Positive(message = "{common.validation.positive}") Long id) {
        PostDto postDto = postService.getPostById(id);
        return ResponseEntity.ok(ApiResult.success(postDto));
    }

    /**
     * 게시글 수정 API.
     * URL: PUT /api/post/{id}
     *
     * @param id          수정할 게시글 ID (양수여야 함)
     * @param postRequest 수정할 데이터 (제목, 내용)
     * @return 수정된 게시글 정보와 HTTP 200 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<PostDto>> updatePost(
            @PathVariable @Positive(message = "{common.validation.positive}") Long id,
            @RequestBody @Valid PostRequest postRequest) {
        PostDto updatedPost = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(ApiResult.success(updatedPost));
    }

    /**
     * 게시글 삭제 API.
     * URL: DELETE /api/post/{id}
     *
     * @param id 삭제할 게시글 ID (양수여야 함)
     * @return HTTP 204 (No Content) 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Long>> deletePost(@PathVariable @Positive(message = "{common.validation.positive}") Long id) {
        this.postService.deletePost(id);
        this.postCommentService.deleteAllPostComments(id);
        return ResponseEntity.ok(ApiResult.success(id));
    }

    /**
     * 특정 게시글에 대해 회원의 좋아요 상태를 토글하는 API.
     * 이미 좋아요한 상태면 좋아요를 취소하고, 그렇지 않으면 좋아요를 추가합니다.
     * URL: POST /api/post/{id}/like
     *
     * @param id 좋아요를 토글할 게시글 ID (양수여야 함)
     * @return 좋아요 상태가 반영된 게시글 정보와 HTTP 200 응답
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResult<PostDto>> toggleLike(
            @PathVariable @Positive(message = "{common.validation.positive}") Long id) {
        PostDto postDto = postService.togglePostLike(id);
        return ResponseEntity.ok(ApiResult.success(postDto));
    }

    /**
     * Page<PostDto>를 PostDtoWithPaging로 변환하는 헬퍼 메서드.
     *
     * @param postsPage 페이징된 게시글 목록
     * @return PostDtoWithPaging 객체
     */
    private PostDtoWithPaging buildPostDtoWithPaging(Page<PostDto> postsPage) {
        return PostDtoWithPaging.builder()
                .totalPages(postsPage.getTotalPages())
                .sizePages(postsPage.getSize())
                .currentPageNumber(postsPage.getNumber())
                .posts(postsPage.getContent())
                .build();
    }
}
