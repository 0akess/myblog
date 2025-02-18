package org.myblog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myblog.dto.CommentDTO;
import org.myblog.model.Comment;
import org.myblog.service.impl.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentViewController {

    private final CommentService commentService;

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Long postId, @ModelAttribute @Valid CommentDTO commentDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Комментарий не может быть пустым");
            return "redirect:/posts/" + postId;
        }

        commentService.createComment(postId, commentDTO.getContent());
        return "redirect:/posts/" + postId;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        var postId = commentService.getCommentById(id);
        commentService.deleteComment(id);
        return ResponseEntity.ok("/posts/" + postId.getPost().getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editComment(@PathVariable Long id, @RequestBody @Valid CommentDTO commentDTO) {
        var postId = commentService.getCommentById(id).getPost().getId();
        commentService.updateComment(id, commentDTO.getContent());
        return ResponseEntity.ok("/posts/" + postId);
    }
}
