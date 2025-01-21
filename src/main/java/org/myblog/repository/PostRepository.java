package org.myblog.repository;

import org.myblog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTagsContaining(String tag, Pageable pageable);

    @NonNull
    Page<Post> findAll(@NonNull Pageable pageable);
}
