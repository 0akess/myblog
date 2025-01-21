package org.myblog.service.api;

import org.myblog.model.Comment;

public interface ICommentService {

    void createComment(Long postId, String content);
    void updateComment(Long id, String content);
    void deleteComment(Long id);
    Comment getCommentById(Long id);
}
