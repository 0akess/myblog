package org.myblog.service.api;

import org.myblog.dto.PostDTO;
import org.myblog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPostService {

    Page<Post> findAll(Pageable pageable);
    Page<Post> getPostsByTag(String tag, Pageable pageable);
    Post findById(Long id);
    void createPost(PostDTO postDTO);
    void updatePost(Long id, PostDTO postDTO);
    void deletePost(Long id);
    void likePost(Long id);
}
