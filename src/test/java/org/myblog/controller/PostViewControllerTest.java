package org.myblog.controller;

import org.myblog.dto.PostDTO;
import org.myblog.model.Post;
import org.myblog.service.impl.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostViewController.class)
class PostViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("Просмотр деталей поста")
    void testViewPostDetails() throws Exception {
        var mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Test Post");
        mockPost.setComments(new ArrayList<>());

        Mockito.when(postService.findById(1L)).thenReturn(mockPost);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post_details"))
                .andExpect(model().attributeExists("post", "comments"))
                .andExpect(model().attribute("post", mockPost))
                .andExpect(model().attribute("comments", mockPost.getComments()));
    }

    @Test
    @DisplayName("Отображение формы создания поста")
    void testShowCreatePostForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    @DisplayName("Создание поста")
    void testCreatePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .param("title", "Новый пост")
                        .param("content", "Содержимое поста"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(postService).createPost(any(PostDTO.class));
    }

    @Test
    @DisplayName("Пост не создается при ошибке валидации")
    void testCreatePostWithValidationError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .param("title", "")
                        .param("content", "Контент"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_post"))
                .andExpect(model().attributeHasFieldErrors("post", "title"));
    }

    @Test
    @DisplayName("Лайк поста")
    void testLikePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/like"))
                .andExpect(status().isOk())
                .andExpect(content().string("/posts/1"));

        Mockito.verify(postService).likePost(1L);
    }

    @Test
    @DisplayName("Удаление поста")
    void testDeletePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("/"));

        Mockito.verify(postService).deletePost(1L);
    }

    @Test
    @DisplayName("Редактирование поста")
    void testUpdatePost() throws Exception {
        var updatedPostJson = """
                {
                    "title": "Обновленный пост",
                    "content": "Обновленное содержимое"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPostJson))
                .andExpect(status().isOk())
                .andExpect(content().string("/posts/1"));

        Mockito.verify(postService).updatePost(anyLong(), any(PostDTO.class));
    }

    @Test
    @DisplayName("Ошибка при просмотре несуществующего поста")
    void testViewNonExistingPostDetails() throws Exception {
        Mockito.when(postService.findById(999L))
                .thenThrow(new RuntimeException("Post is not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post is not found"));

        Mockito.verify(postService).findById(999L);
    }

    @Test
    @DisplayName("Ошибка при создании поста с пустым содержанием")
    void testCreatePostWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .param("title", "Тестовый пост")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("create_post"))
                .andExpect(model().attributeHasFieldErrors("post", "content"));
    }

    @Test
    @DisplayName("Ошибка при лайке несуществующего поста")
    void testLikeNonExistingPost() throws Exception {
        Mockito.doThrow(new RuntimeException("Post is not found")).when(postService).likePost(999L);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/999/like"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post is not found"));

        Mockito.verify(postService).likePost(999L);
    }

    @Test
    @DisplayName("Ошибка при редактировании несуществующего поста")
    void testUpdateNonExistingPost() throws Exception {
        var updatedPostJson = """
            {
                "title": "Не существующий пост",
                "content": "Контент"
            }
            """;

        Mockito.doThrow(new RuntimeException("Post is not found"))
                .when(postService).updatePost(eq(999L), any(PostDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPostJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post is not found"));

        Mockito.verify(postService).updatePost(eq(999L), any(PostDTO.class));
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего поста")
    void testDeleteNonExistingPost() throws Exception {
        Mockito.doThrow(new RuntimeException("Post is not found"))
                .when(postService).deletePost(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post is not found"));

        Mockito.verify(postService).deletePost(999L);
    }

    @Test
    @DisplayName("Редирект после успешного создания поста")
    void testRedirectAfterCreatePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .param("title", "Тестовый пост")
                        .param("content", "Тестовый контент"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(postService).createPost(any(PostDTO.class));
    }
}

