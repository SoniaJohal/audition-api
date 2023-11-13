package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;

    public List<AuditionPost> getPosts(final int page, final int pageSize) {
        final List<AuditionPost> allPosts =  auditionIntegrationClient.getPosts();
        return getPaginatedResults(allPosts, page, pageSize);
    }

    private List<AuditionPost> getPaginatedResults(final List<AuditionPost> posts, final int page, final int size) {
        if (posts.size() <= size) {
            return posts;
        }

        int totalPages = (int) Math.ceil((double) posts.size() / size);

        // If page is greater than totalPages, then set page to total number of pages (last page)
        int pageToReturn = page > totalPages ? totalPages : page;

        final int fromIndex = (pageToReturn - 1) * size;
        final int toIndex = Math.min(fromIndex + size, posts.size());
        return posts.subList(fromIndex, toIndex);
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

}
