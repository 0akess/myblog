package org.myblog.controller;

import org.myblog.model.Post;
import org.myblog.service.impl.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(HomePageViewController.class)
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("Отображение главной страницы с постами")
    void testViewHomePage() throws Exception {
        var mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Test Post");
        mockPost.setContent("Test Content");
        mockPost.setComments(new ArrayList<>());

        Mockito.when(postService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockPost)));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .param("page", "0")
                        .param("size", "10")
                        .param("tag", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts", "currentPage", "totalPages", "selectedTag", "size"))
                .andExpect(model().attribute("selectedTag", ""))
                .andExpect(model().attribute("posts", Matchers.hasSize(1)))
                .andExpect(model().attribute("posts", Matchers.hasItem(
                        Matchers.<Post>hasProperty("title", Matchers.is("Test Post"))
                )));
    }

    @Test
    @DisplayName("Отображение главной страницы без постов")
    void testViewHomePageWithoutPosts() throws Exception {
        Mockito.when(postService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("Фильтрация постов по тегу")
    void testViewHomePageWithTag() throws Exception {
        var taggedPost = new Post();
        taggedPost.setId(2L);
        taggedPost.setTitle("Tagged Post");
        taggedPost.setContent("Content with tag");
        taggedPost.setTags(List.of("Java"));

        Mockito.when(postService.getPostsByTag(Mockito.eq("Java"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(taggedPost)));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .param("tag", "Java"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", Matchers.hasSize(1)))
                .andExpect(model().attribute("posts", Matchers.hasItem(
                        Matchers.<Post>hasProperty("title", Matchers.is("Tagged Post"))
                )))
                .andExpect(model().attribute("selectedTag", "Java"));
    }

    @Test
    @DisplayName("Переход на вторую страницу")
    void testPaginationSecondPage() throws Exception {
        var secondPagePost = new Post();
        secondPagePost.setId(3L);
        secondPagePost.setTitle("Second Page Post");
        secondPagePost.setContent("Second page content");

        Mockito.when(postService.findAll(PageRequest.of(1, 10)))
                .thenReturn(new PageImpl<>(List.of(secondPagePost)));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("posts", Matchers.hasSize(1)))
                .andExpect(model().attribute("posts", Matchers.hasItem(
                        Matchers.<Post>hasProperty("title", Matchers.is("Second Page Post"))
                )));
    }

    @Test
    @DisplayName("Тег null не вызывает ошибок")
    void testViewHomePageWithNullTag() throws Exception {
        Mockito.when(postService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .param("tag", (String) null))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", Matchers.hasSize(0)));
    }

}
