package com.example.demo.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostCommentContentRequest {
    private Long postCommentId;
    private String Content;
}
