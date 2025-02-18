package org.myblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.myblog.model.Comment;
import org.myblog.model.Post;
import org.myblog.repository.CommentRepository;
import org.myblog.service.impl.CommentService;
import org.myblog.service.impl.PostService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    private Post mockPost;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Test Post");

        mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setPost(mockPost);
        mockComment.setContent("Test Comment");
        mockComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Поиск комментария по ID - успешный")
    void testGetCommentById_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));

        var foundComment = commentService.getCommentById(1L);

        assertNotNull(foundComment);
        assertEquals("Test Comment", foundComment.getContent());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Поиск комментария по ID - не найден")
    void testGetCommentById_NotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(
                RuntimeException.class, () -> commentService.getCommentById(1L)
        );
        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Создание комментария к посту")
    void testCreateComment() {
        when(postService.findById(1L)).thenReturn(mockPost);

        commentService.createComment(1L, "Новый комментарий");

        verify(postService, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Удаление комментария - успешное")
    void testDeleteComment_Success() {
        when(commentRepository.existsById(1L)).thenReturn(true);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).existsById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление комментария - не найден")
    void testDeleteComment_NotFound() {
        when(commentRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(
                RuntimeException.class,
                () -> commentService.deleteComment(1L)
        );
        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository, times(1)).existsById(1L);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Обновление комментария - успешное")
    void testUpdateComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));

        commentService.updateComment(1L, "Обновленный комментарий");

        assertEquals("Обновленный комментарий", mockComment.getContent());
        verify(commentRepository, times(1)).save(mockComment);
    }

    @Test
    @DisplayName("Обновление комментария - не найден")
    void testUpdateComment_NotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(
                RuntimeException.class,
                () -> commentService.updateComment(1L, "Обновленный комментарий")
        );
        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}

