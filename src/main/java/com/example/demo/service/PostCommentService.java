package com.example.demo.service;

import com.example.demo.dto.post.CreatePostCommentRequest;
import com.example.demo.dto.post.PostCommentDto;

import java.util.List;

public interface PostCommentService {
    List<PostCommentDto> findAllPostComments(Long postId);
    PostCommentDto createPostComment(CreatePostCommentRequest createPostCommentRequest);
    void deletePostComment(Long id);
    void deleteAllPostComments(Long postId);
}
