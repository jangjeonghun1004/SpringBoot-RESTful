package com.example.demo.service;

import com.example.demo.dto.post.PostDto;
import com.example.demo.dto.post.PostRequest;
import com.example.demo.entity.Member;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.provider.MessageProvider;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 자유 게시판 서비스 구현 클래스
 *
 * 이 클래스는 자유 게시판 관련 비즈니스 로직을 처리하는 서비스 레이어로,
 * 게시글 작성 및 수정, 게시글 조회, 게시글 삭제, 게시글 좋아요 등의 기능을 제공한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param postRequest 클라이언트 요청 데이터
     * @return 생성된 게시글에 대한 응답 DTO
     */
    @Override
    public PostDto createPost(final PostRequest postRequest) {
        Member currentMember = getCurrentMember();
        // 게시글 저장 및 영속화
        Post savedPost = postRepository.save(
                Post.builder()
                        .title(postRequest.getTitle())
                        .content(postRequest.getContent())
                        .member(currentMember)
                        .build()
        );
        return convertToPostDto(savedPost);
    }

    /**
     * 전체 게시글 목록을 페이징 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 게시글 응답 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<PostDto> findAllPosts(final Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertToPostDto);
    }

    /**
     * 게시글 제목에 키워드를 포함하는 게시글을 검색합니다.
     *
     * @param title    검색할 키워드
     * @param pageable 페이징 및 정렬 정보
     * @return 검색 결과 페이지
     */
    @Transactional(readOnly = true)
    public Page<PostDto> searchPostsByTitle(final String title, final Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::convertToPostDto);
    }

    /**
     * 게시글 ID를 기반으로 단일 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글 응답 DTO
     * @throws ResourceNotFoundException 게시글이 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public PostDto getPostById(final Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + id));
        return convertToPostDto(post);
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param id          수정할 게시글 ID
     * @param postRequest 수정할 데이터
     * @return 수정된 게시글 응답 DTO
     * @throws ResourceNotFoundException 게시글이 존재하지 않을 경우 예외 발생
     */
    @Override
    public PostDto updatePost(final Long id, final PostRequest postRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + id));
        post.updatePost(postRequest.getTitle(), postRequest.getContent());
        return convertToPostDto(post);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 게시글 ID
     * @throws ResourceNotFoundException 게시글이 존재하지 않을 경우 예외 발생
     */
    @Override
    public void deletePost(final Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + id));
        postRepository.delete(post);
    }

    /**
     * 특정 게시글에 대해 회원의 좋아요 상태를 토글합니다.
     * 이미 좋아요한 상태이면 좋아요를 취소하고, 그렇지 않으면 좋아요를 추가합니다.
     *
     * @param id 게시글 ID
     * @return 좋아요 상태가 반영된 게시글 응답 DTO
     * @throws ResourceNotFoundException 게시글이 존재하지 않을 경우 예외 발생
     */
    @Override
    public PostDto togglePostLike(final Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + id));

        Long currentMemberId = getCurrentMemberId();
        Optional<PostLike> optionalPostLike = postLikeRepository.findPostLikeByPostIdAndMemberId(id, currentMemberId);

        if (optionalPostLike.isPresent()) {
            // 이미 좋아요한 상태: 좋아요 취소
            postLikeRepository.delete(optionalPostLike.get());
            post.decrementLikeCount();
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .memberId(currentMemberId)
                    .build();
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
        }
        return convertToPostDto(post);
    }

    /**
     * 현재 인증된 사용자의 Member 엔티티를 반환하는 헬퍼 메서드.
     *
     * @return 현재 인증된 Member
     * @throws IllegalStateException 인증 정보가 없거나 사용자를 찾을 수 없을 경우 예외 발생
     */
    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(MessageProvider.getMessage("common.validation.noAuthenticated"));
        }
        return memberRepository.findMemberByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " email: " + authentication.getName()));
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * @return 현재 사용자의 ID
     */
    private Long getCurrentMemberId() {
        return getCurrentMember().getId();
    }

    /**
     * Post 엔티티를 PostDto로 변환하는 헬퍼 메서드.
     *
     * @param post 변환할 Post 엔티티
     * @return 변환된 PostDto
     */
    private PostDto convertToPostDto(final Post post) {
        boolean likedByUser = postLikeRepository.existsByPostIdAndMemberId(post.getId(), getCurrentMemberId());
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getLikeCount().get())
                .likedByUser(likedByUser)
                .build();
    }
}
