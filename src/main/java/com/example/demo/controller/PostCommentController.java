package com.example.demo.controller;

import com.example.demo.dto.ApiResult;
import com.example.demo.dto.post.CreatePostCommentRequest;
import com.example.demo.dto.post.PostCommentDto;
import com.example.demo.service.PostCommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/postComment")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<ApiResult<PostCommentDto>> createPostComment(@RequestBody @Valid CreatePostCommentRequest createPostCommentRequest) {
        PostCommentDto savedPostComment = this.postCommentService.createPostComment(createPostCommentRequest);
        return ResponseEntity.ok(ApiResult.success(savedPostComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostComment(@PathVariable @Positive(message = "{common.validation.positive}") Long id) {
        this.postCommentService.deletePostComment(id);
        return ResponseEntity.noContent().build();
    }
}
