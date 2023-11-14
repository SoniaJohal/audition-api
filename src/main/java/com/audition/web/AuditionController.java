package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuditionController {

    private final AuditionService auditionService;

    private static final int MAX_PAGE_SIZE = 25;

    @Autowired
    public AuditionController(final AuditionService auditionService) {
        this.auditionService = auditionService;
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam(defaultValue = "1") @Min(1) final int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(value = MAX_PAGE_SIZE, message = "Page size cannot be greater than " + MAX_PAGE_SIZE) final int pageSize) {
        return auditionService.getPosts(page, pageSize);
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPosts(
        @PathVariable("id") @NotBlank @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostWithComments(
        @PathVariable("id") @NotBlank @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getPostWithComments(postId);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<PostComment> getCommentsForPost(
        @RequestParam("postId") @NotBlank @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getCommentsByPostId(postId);
    }
}
