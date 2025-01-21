package org.myblog.service;

import org.myblog.dto.PostDTO;
import org.myblog.mapper.PostMapper;
import org.myblog.model.Post;
import org.myblog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.myblog.service.impl.PostService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Post mockPost;
    private PostDTO mockPostDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Test Post");
        mockPost.setContent("Test Content");

        mockPostDTO = new PostDTO();
        mockPostDTO.setTitle("Test Post");
        mockPostDTO.setContent("Test Content");
    }

    @Test
    @DisplayName("Получение всех постов с пагинацией")
    void testFindAllPosts() {
        var postPage = new PageImpl<>(List.of(mockPost));
        when(postRepository.findAll(any(PageRequest.class))).thenReturn(postPage);

        var result = postService.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        verify(postRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Получение поста по ID")
    void testFindPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        var result = postService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Test Post");
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка при получении несуществующего поста")
    void testFindPostById_NotFound() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.findById(2L));
        verify(postRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Создание нового поста")
    void testCreatePost() {
        when(postMapper.toEntity(any(PostDTO.class))).thenReturn(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        postService.createPost(mockPostDTO);

        verify(postMapper, times(1)).toEntity(any(PostDTO.class));
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Удаление поста по ID")
    void testDeletePost() {
        postService.deletePost(1L);

        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Добавление лайка к посту")
    void testLikePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.likePost(1L);

        assertThat(mockPost.getLikes()).isEqualTo(1);
        verify(postRepository, times(1)).save(mockPost);
    }

    @Test
    @DisplayName("Ошибка при лайке несуществующего поста")
    void testLikePost_NotFound() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.likePost(2L));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Поиск постов по тегу")
    void testGetPostsByTag() {
        var postPage = new PageImpl<>(List.of(mockPost));
        when(postRepository.findByTagsContaining(eq("test"), any(PageRequest.class))).thenReturn(postPage);

        var result = postService.getPostsByTag("test", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        verify(postRepository, times(1)).findByTagsContaining(eq("test"), any(PageRequest.class));
    }

    @Test
    @DisplayName("Обновление поста")
    void testUpdatePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(postMapper.toEntity(any(PostDTO.class))).thenReturn(mockPost);

        postService.updatePost(1L, mockPostDTO);

        verify(postRepository, times(1)).save(mockPost);
    }

    @Test
    @DisplayName("Ошибка при обновлении несуществующего поста")
    void testUpdatePost_NotFound() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.updatePost(2L, mockPostDTO));
        verify(postRepository, never()).save(any(Post.class));
    }
}
