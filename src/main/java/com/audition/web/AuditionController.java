package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import com.audition.service.AuditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Get all posts")
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
        @Parameter(description = "Filters the posts on Title", required = false) @RequestParam(required = false) final String filter,
        @Parameter(description = "The page number to retrieve posts from", required = false) @RequestParam(defaultValue = "1") @Min(1) final int page,
        @Parameter(description = "Number of posts per page", required = false) @RequestParam(defaultValue = "10") @Min(1)
        @Max(value = MAX_PAGE_SIZE, message = "Page size cannot be greater than " + MAX_PAGE_SIZE)
        final int pageSize) {
        if (filter != null) {
            return auditionService.getPostsFilteredByTitle(filter, page, pageSize);
        }
        return auditionService.getPosts(page, pageSize);
    }

    @Operation(summary = "Get posts by Id")
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostById(
        @Parameter(description = "Unique identifier retrieve the posts ", required = true) @PathVariable("id") @NotBlank
        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getPostById(postId);
    }

    @Operation(summary = "Get a post with comments")
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostWithComments(
        @Parameter(description = "Unique identifier retrieve the posts ", required = true)  @PathVariable("id") @NotBlank
        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getPostWithComments(postId);
    }

    @Operation(summary = "Get comments for a post")
    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<PostComment> getCommentsForPost(
        @Parameter(description = "Unique identifier of the post to retrieve comments for ", required = true) @RequestParam("postId") @NotBlank
        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "postId must be a valid integer")
        @Min(1) final String postId) {
        return auditionService.getCommentsByPostId(postId);
    }
}
