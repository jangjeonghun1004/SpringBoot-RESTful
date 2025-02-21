package com.example.demo.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostDtoWithPaging {
    List<PostDto> posts;
    int totalPages;
    int sizePages;
    int currentPageNumber;
}
