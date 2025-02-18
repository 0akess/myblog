package org.myblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myblog.dto.PostDTO;
import org.myblog.model.Post;
import org.myblog.service.impl.PostService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostViewController {

    private final PostService postService;

    @GetMapping("/{id}")
    public String viewPostDetails(@PathVariable Long id, Model model) {
        var post = postService.findById(id);
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("comments", post.getComments());
        return "post_details";
    }

    @GetMapping("/new")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new PostDTO());
        return "create_post";
    }

    @PostMapping
    public String createPost(@ModelAttribute("post") @Valid PostDTO postDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "create_post";
        }
        postService.createPost(postDTO);
        return "redirect:/";
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable Long id) {
        postService.likePost(id);
        return ResponseEntity.ok("/posts/" + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("/");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody @Valid PostDTO updatedPost, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            DefaultMessageSourceResolvable::getDefaultMessage
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        postService.updatePost(id, updatedPost);
        return ResponseEntity.ok("/posts/" + id);
    }

}

