package org.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostDTO {

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
    private String title;

    @NotBlank(message = "Содержимое не может быть пустым")
    private String content;

    private String imageUrl;

    private List<String> tags = new ArrayList<>();
}

