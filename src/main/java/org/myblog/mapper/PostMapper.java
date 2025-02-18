package org.myblog.mapper;

import org.myblog.dto.PostDTO;
import org.myblog.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper implements Mapper<PostDTO, Post>{

    @Override
    public Post toEntity(PostDTO dto) {
        var post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());
        post.setTags(dto.getTags());
        return post;
    }

    @Override
    public PostDTO toDto(Post post) {
        var dto = new PostDTO();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setTags(dto.getTags());
        return dto;
    }
}
