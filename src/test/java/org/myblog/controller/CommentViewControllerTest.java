package org.myblog.controller;

import org.myblog.model.Comment;
import org.myblog.model.Post;
import org.myblog.service.impl.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentViewController.class)
class CommentViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("Добавление комментария к посту")
    void testAddComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/add/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("content", "Новый комментарий"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        Mockito.verify(commentService).createComment(1L, "Новый комментарий");
    }

    @Test
    @DisplayName("Удаление комментария по ID")
    void testDeleteComment() throws Exception {
        var mockPost = new Post();
        mockPost.setId(1L);

        var mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setPost(mockPost);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(mockComment);

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("/posts/1"));

        Mockito.verify(commentService).deleteComment(1L);
    }

    @Test
    @DisplayName("Редактирование комментария")
    void testEditComment() throws Exception {
        var mockPost = new Post();
        mockPost.setId(1L);

        var mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setPost(mockPost);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(mockComment);

        var updatedContent = "{\"content\": \"Обновленный комментарий\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/comments/1")
                        .content(updatedContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("/posts/1"));

        Mockito.verify(commentService).updateComment(1L, "Обновленный комментарий");
    }

    @Test
    @DisplayName("Ошибка при добавлении пустого комментария")
    void testAddEmptyComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/add/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("content", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        Mockito.verify(commentService, Mockito.never()).createComment(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего комментария")
    void testDeleteNonExistingComment() throws Exception {
        Mockito.when(commentService.getCommentById(999L))
                .thenThrow(new RuntimeException("Comment not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));

        Mockito.verify(commentService, Mockito.never()).deleteComment(999L);
    }

    @Test
    @DisplayName("Ошибка при редактировании комментария с пустым содержимым")
    void testEditCommentWithEmptyContent() throws Exception {
        var mockPost = new Post();
        mockPost.setId(1L);

        var mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setPost(mockPost);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(mockComment);

        var invalidContent = "{\"content\": \"\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/comments/1")
                        .content(invalidContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(commentService, Mockito.never()).updateComment(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    @DisplayName("Ошибка при удалении комментария без ID")
    void testDeleteCommentWithoutId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/"))
                .andExpect(status().is4xxClientError());

        Mockito.verify(commentService, Mockito.never()).deleteComment(Mockito.anyLong());
    }

    @Test
    @DisplayName("Ошибка при редактировании несуществующего комментария")
    void testEditNonExistingComment() throws Exception {
        Mockito.when(commentService.getCommentById(999L))
                .thenThrow(new RuntimeException("Comment not found"));

        var updatedContent = "{\"content\": \"Новый текст\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/comments/999")
                        .content(updatedContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(commentService, Mockito.never()).updateComment(Mockito.anyLong(), Mockito.anyString());
    }
}

