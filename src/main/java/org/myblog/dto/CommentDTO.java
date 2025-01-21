package org.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    @NotBlank(message = "Комментарий не может быть пустым")
    private String content;
}
