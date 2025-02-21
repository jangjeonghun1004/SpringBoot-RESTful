package com.example.demo.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 생성 및 수정 요청을 위한 DTO.
 * 클라이언트로부터 전달받은 데이터의 유효성을 검증합니다.
 */
@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "title: {common.validation.notBlank}")
    private String title;

    @NotBlank(message = "content: {common.validation.notBlank}")
    private String content;

}
