package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Value("${audition.api.posts.url}")
    private String postsApiBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        final String url = postsApiBaseUrl + "/posts";
        ResponseEntity<AuditionPost[]> responseEntity = restTemplate.getForEntity(url, AuditionPost[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public AuditionPost getPostById(final String id) {
        final String postByIdUrl = postsApiBaseUrl + "/posts/" + id;
        try {
           return restTemplate.getForObject(postByIdUrl, AuditionPost.class);
        } catch (final HttpClientErrorException e) {
            handleHttpClientErrorException(e, id);
        }
        return null;
    }

    private void handleHttpClientErrorException(final HttpClientErrorException e, final String id) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", HttpStatus.NOT_FOUND.value());
        }
        throw e;
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
}
