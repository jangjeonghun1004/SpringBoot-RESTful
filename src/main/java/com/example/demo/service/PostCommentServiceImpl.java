package com.example.demo.service;

import com.example.demo.dto.post.CreatePostCommentRequest;
import com.example.demo.dto.post.PostCommentDto;
import com.example.demo.entity.Member;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostComment;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.StandardException;
import com.example.demo.provider.MessageProvider;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostCommentRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 댓글(Comment) 서비스 구현 클래스
 * <p>
 * 게시글에 댓글 추가, 조회 및 삭제 기능을 제공합니다.
 * <p>
 * 적용 URL:
 * <ul>
 *   <li>POST /api/post/{postId}/comments : 댓글 추가</li>
 *   <li>GET /api/post/{postId}/comments?memberId={memberId} : 댓글 목록 조회</li>
 *   <li>DELETE /api/post/comments/{id} : 댓글 삭제</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AuthenticationFacade authenticationFacade;

    /**
     * PostComment 엔티티를 PostCommentDto로 변환합니다.
     *
     * @param postComment 변환할 PostComment 엔티티
     * @return 변환된 PostCommentDto
     */
    private PostCommentDto convertToPostCommentDto(PostComment postComment) {
        boolean isEnabledDelete = this.authenticationFacade.getCurrentMemberId().equals(postComment.getMember().getId());

        return PostCommentDto.builder()
                .id(postComment.getId())
                .content(postComment.getContent())
                .postId(postComment.getPost().getId())
                .memberId(postComment.getMember().getId())
                .memberEmail(postComment.getMember().getEmail())
                .createdAt(postComment.getCreatedAt())
                .updatedAt(postComment.getUpdatedAt())
                .isEnabledDelete(isEnabledDelete)
                .build();
    }

    /**
     * 특정 게시글의 모든 댓글을 조회합니다.
     * <p>
     * URL: GET /api/post/{postId}/comments?memberId={memberId}
     *
     * @param postId 조회할 게시글 ID (양수여야 함)
     * @return 해당 게시글에 속한 댓글 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostCommentDto> findAllPostComments(Long postId) {
        return postCommentRepository.findByPostId(postId)
                .stream()
                .map(this::convertToPostCommentDto)
                .toList();
    }

    /**
     * 특정 게시글에 댓글을 추가합니다.
     * <p>
     * URL: POST /api/post/{postId}/comments
     *
     * @param request 댓글 추가 요청 데이터 (게시글 ID, 댓글 내용 등)
     * @return 추가된 댓글 정보를 담은 PostCommentDto
     * @throws ResourceNotFoundException 게시글 또는 사용자가 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public PostCommentDto createPostComment(CreatePostCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + request.getPostId()));
        Member member = memberRepository.findById(authenticationFacade.getCurrentMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + authenticationFacade.getCurrentMemberId()));
        PostComment postComment = PostComment.builder()
                .content(request.getContent())
                .post(post)
                .member(member)
                .build();
        PostComment savedComment = postCommentRepository.save(postComment);
        return convertToPostCommentDto(savedComment);
    }

    /**
     * 특정 댓글을 삭제합니다.
     * <p>
     * URL: DELETE /api/post/comments/{id}
     *
     * @param id 삭제할 댓글 ID (양수여야 함)
     * @throws ResourceNotFoundException 삭제할 댓글이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public void deletePostComment(Long id) {
        PostComment postComment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageProvider.getMessage("common.validation.resourceNotFoundException") + " id: " + id)
                );

        if(!postComment.getMember().getId().equals(this.authenticationFacade.getCurrentMemberId())) {
            throw new StandardException(MessageProvider.getMessage("common.validation.doNotHavePermission"));
        }

        postCommentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllPostComments(Long postId) {
        this.postCommentRepository.deleteByPostId(postId);
    }
}
