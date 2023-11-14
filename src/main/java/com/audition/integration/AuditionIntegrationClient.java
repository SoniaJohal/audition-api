package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AuditionIntegrationClient {

    private final String postsApiBaseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AuditionIntegrationClient(final RestTemplate restTemplate, @Value("${audition.api.posts.url}") final String postsApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.postsApiBaseUrl = postsApiBaseUrl;
    }

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

    public AuditionPost getPostWithComments(final String id) {
        final String postByIdUrl = postsApiBaseUrl + "/posts/" + id +"/comments";
        try {
            final AuditionPost auditionpost = getPostById(id);
            final ResponseEntity<PostComment[]> responseEntity = restTemplate.getForEntity(postByIdUrl, PostComment[].class);
            final List<PostComment> postComments = Arrays.asList(responseEntity.getBody());
            auditionpost.setComments(postComments);
            return auditionpost;
        } catch (final HttpClientErrorException e) {
            handleHttpClientErrorException(e, id);
        }
        return null;
    }

    public List<PostComment> getCommentsByPostId(final String id) {
        final String postByIdUrl = postsApiBaseUrl + "/comments"  ;
        String url = UriComponentsBuilder.fromUriString(postByIdUrl)
            .queryParam("postId", id)
            .build()
            .toUriString();

       ResponseEntity<PostComment[]> responseEntity = restTemplate.getForEntity(url, PostComment[].class);
       return Arrays.asList(responseEntity.getBody());
    }

    private void handleHttpClientErrorException(final HttpClientErrorException e, final String id) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", HttpStatus.NOT_FOUND.value());
        }
        throw e;
    }
}
