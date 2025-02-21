package com.example.demo.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO.
 * 클라이언트에게 전달할 게시글 정보를 담습니다.
 */
@Getter
@Setter
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private int likeCount;       // 전체 좋아요 수
    private boolean likedByUser; // 현재 사용자가 좋아요한 상태 (true: 좋아요 누른 상태, false: 좋아요 안 누른 상태)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
