package org.myblog.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.myblog.model.Post;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void whenSavePost_thenItIsSaved() {
        var post = new Post();
        post.setTitle("Тестовый пост");
        post.setContent("Контент поста");

        var savedPost = postRepository.save(post);

        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("Тестовый пост");
    }

    @Test
    void whenFindById_thenReturnPost() {
        var post = new Post();
        post.setTitle("Найди меня");
        post.setContent("Контент");

        postRepository.save(post);

        var foundPost = postRepository.findById(post.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Найди меня");
    }

    @Test
    void whenUpdatePost_thenContentIsUpdated() {
        var post = new Post();
        post.setTitle("Обновление");
        post.setContent("Старый контент");

        var savedPost = postRepository.save(post);

        savedPost.setContent("Новый контент");
        postRepository.save(savedPost);

        var updatedPost = postRepository.findById(savedPost.getId());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getContent()).isEqualTo("Новый контент");
    }

    @Test
    void whenDeletePost_thenItIsDeleted() {
        var post = new Post();
        post.setTitle("Удалить меня");
        post.setContent("Контент");

        var savedPost = postRepository.save(post);

        postRepository.delete(savedPost);

        var deletedPost = postRepository.findById(savedPost.getId());
        assertThat(deletedPost).isNotPresent();
    }

    @Test
    void whenFindByTagWithPagination_thenReturnPagedPosts() {
        var post1 = new Post();
        post1.setTitle("Пост с тегом Java 1");
        post1.setContent("Контент 1");
        post1.setTags(List.of("Java", "Spring"));

        var post2 = new Post();
        post2.setTitle("Пост с тегом Java 2");
        post2.setContent("Контент 2");
        post2.setTags(List.of("Java"));

        var post3 = new Post();
        post3.setTitle("Пост с тегом Python");
        post3.setContent("Контент 3");
        post3.setTags(List.of("Python"));

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        var pageable = PageRequest.of(0, 2);

        var javaPosts = postRepository.findByTagsContaining("Java", pageable);

        assertThat(javaPosts.getContent()).hasSize(2);
        assertThat(javaPosts.getContent().getFirst().getTitle()).contains("Java");
        assertThat(javaPosts.getTotalElements()).isEqualTo(2);
    }

    @Test
    void whenFindAll_thenReturnAllPosts() {
        var post1 = new Post();
        post1.setTitle("Пост 1");
        post1.setContent("Контент 1");

        var post2 = new Post();
        post2.setTitle("Пост 2");
        post2.setContent("Контент 2");

        postRepository.save(post1);
        postRepository.save(post2);

        var allPosts = postRepository.findAll();

        assertThat(allPosts).hasSize(2);
    }
}

