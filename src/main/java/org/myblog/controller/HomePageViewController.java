package org.myblog.controller;

import lombok.RequiredArgsConstructor;
import org.myblog.model.Post;
import org.myblog.service.impl.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomePageViewController {

    private final PostService postService;

    @GetMapping("/")
    public String viewHomePage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String tag,
                               Model model) {
        if (page < 0 || !(size == 10 || size == 20 || size == 50)) {
            throw new IllegalArgumentException("Неверные параметры пагинации. Допустимые значения: 10, 20, 50.");
        }

        Page<Post> posts;
        if (tag != null && !tag.isBlank()) {
            posts = postService.getPostsByTag(tag, PageRequest.of(page, size));
        } else {
            posts = postService.findAll(PageRequest.of(page, size));
        }

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("selectedTag", tag);
        model.addAttribute("size", size);

        return "index";
    }
}
