package org.myblog.service.impl;

import lombok.RequiredArgsConstructor;
import org.myblog.model.Comment;
import org.myblog.repository.CommentRepository;
import org.myblog.service.api.ICommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    public void createComment(Long postId, String content) {
        var post = postService.findById(postId);
        var comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void updateComment(Long commentId, String newContent) {
        var comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(newContent);
        commentRepository.save(comment);
    }
}
