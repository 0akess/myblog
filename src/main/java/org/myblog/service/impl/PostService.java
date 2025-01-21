package org.myblog.service.impl;

import lombok.RequiredArgsConstructor;
import org.myblog.dto.PostDTO;
import org.myblog.mapper.PostMapper;
import org.myblog.model.Post;
import org.myblog.repository.PostRepository;
import org.myblog.service.api.IPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Post findById(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Post is not found"));
    }

    @Override
    public void createPost(PostDTO postDTO) {
        postRepository
                .save(postMapper.toEntity(postDTO));
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void likePost(Long id) {
        var post = postRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Post is not found"));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    @Override
    public Page<Post> getPostsByTag(String tag, Pageable pageable) {
        return postRepository
                .findByTagsContaining(tag, pageable);
    }

    @Override
    public void updatePost(Long id, PostDTO postDTO) {
        var existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post is not found: " + id));

        var updatedPost = postMapper.toEntity(postDTO);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setImageUrl(updatedPost.getImageUrl());
        existingPost.setTags(updatedPost.getTags());

        postRepository.save(existingPost);
    }
}
