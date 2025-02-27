package com.example.demo.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostCommentRequest {
    @Positive(message = "postId: {common.validation.positive}")
    private Long postId;

    @NotBlank(message = "Content: {common.validation.notBlank}")
    private String Content;
}
