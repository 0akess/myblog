package org.myblog.repository;

import org.myblog.model.Comment;
import org.myblog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Post mockPost;

    @BeforeEach
    void setUp() {
        mockPost = Mockito.mock(Post.class);
        Mockito.when(mockPost.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("Сохранение комментария")
    void testSaveComment() {
        var comment = new Comment();
        comment.setContent("Тестовый комментарий");
        comment.setPost(mockPost);

        var savedComment = commentRepository.save(comment);

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Тестовый комментарий");
    }

    @Test
    @DisplayName("Поиск комментария по ID")
    void testFindById() {
        var comment = new Comment();
        comment.setContent("Комментарий для поиска");
        comment.setPost(mockPost);

        var savedComment = commentRepository.save(comment);

        Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("Комментарий для поиска");
    }

    @Test
    @DisplayName("Обновление комментария")
    void testUpdateComment() {
        var comment = new Comment();
        comment.setContent("Старый текст");
        comment.setPost(mockPost);

        var savedComment = commentRepository.save(comment);

        savedComment.setContent("Новый текст");
        var updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment.getContent()).isEqualTo("Новый текст");
    }

    @Test
    @DisplayName("Удаление комментария")
    void testDeleteComment() {
        var comment = new Comment();
        comment.setContent("Комментарий для удаления");
        comment.setPost(mockPost);

        var savedComment = commentRepository.save(comment);

        commentRepository.delete(savedComment);

        var deletedComment = commentRepository.findById(savedComment.getId());

        assertThat(deletedComment).isNotPresent();
    }
}


