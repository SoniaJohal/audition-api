package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private final AuditionIntegrationClient auditionIntegrationClient;

    @Autowired
    public AuditionService(final AuditionIntegrationClient auditionIntegrationClient) {
        this.auditionIntegrationClient = auditionIntegrationClient;
    }

    /**
     * Get all posts limited by pagination params.
     *
     * @param page - The page number for pagination.
     * @param pageSize - pageSize The size of each page.
     * @return A list of AuditionPost objects representing posts based on pagination.
     */
    public List<AuditionPost> getPosts(final int page, final int pageSize) {
        final List<AuditionPost> allPosts =  auditionIntegrationClient.getPosts();
        return getPaginatedResults(allPosts, page, pageSize);
    }

    /**
     * Get all filtered posts limited by pagination params.
     *
     * @param filter - The string to filter posts by title.
     * @param page - The page number for pagination.
     * @param size - The size of each page.
     * @return A list of AuditionPost objects representing filtered posts based on pagination.
     */
    public List<AuditionPost> getPostsFilteredByTitle(final String filter, final int page, final int size) {
        final List<AuditionPost> allPosts =  auditionIntegrationClient.getPosts();

        final List<AuditionPost> filteredPosts = allPosts.stream()
            .filter(post -> post.getTitle().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)))
            .collect(Collectors.toList());

        return getPaginatedResults(filteredPosts, page, size);
    }

    /**
     * Get post by postId.
     *
     * @param postId of the post to retrieve.
     * @return Post whose id is postId.
     */
    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    /**
     * Get comments for a given post.
     *
     * @param postId of the post whose whose comments need to be retrieved.
     * @return List of comments for a given post.
     */
    public List<PostComment> getCommentsByPostId(final String postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }

    /**
     * Get Post populated with comments.
     *
     * @param postId of the post to retrieve.
     * @return Post whose id is postId populated with comments.
     */
    public AuditionPost getPostWithComments(final String postId) {
        return auditionIntegrationClient.getPostWithComments(postId);
    }

    private List<AuditionPost> getPaginatedResults(final List<AuditionPost> posts, final int page, final int size) {
        if (posts.size() <= size) {
            return posts;
        }

        final int totalPages = (int) Math.ceil((double) posts.size() / size);

        // If page is greater than totalPages, then set page to total number of pages (last page)
        final int pageToReturn = page > totalPages ? totalPages : page;

        final int fromIndex = (pageToReturn - 1) * size;
        final int toIndex = Math.min(fromIndex + size, posts.size());
        return posts.subList(fromIndex, toIndex);
    }

}
