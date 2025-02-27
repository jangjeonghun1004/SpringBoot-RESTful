package com.example.demo.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostCommentDto {
    private Long id;
    private String content;
    private Long memberId;
    private String memberEmail;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isEnabledDelete;
}
