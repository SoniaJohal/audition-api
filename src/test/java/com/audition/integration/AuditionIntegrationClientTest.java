package com.audition.integration;

import static com.audition.util.AuditionApiTestUtil.buildAuditionPost;
import static com.audition.util.AuditionApiTestUtil.buildAuditionPostsArray;
import static com.audition.util.AuditionApiTestUtil.buildPostCommentsArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    private AuditionIntegrationClient  auditionIntegrationClient;

    private static final String BASE_URL = "http://example.com/api";
    private static final String POSTS_URL = BASE_URL + "/posts/";
    private static final String POST_ID = "1";

    @BeforeEach
    public void setUp() {
        auditionIntegrationClient = new AuditionIntegrationClient(restTemplate, BASE_URL);
    }

    @Test
    void shouldFetchAllPosts() {
        final AuditionPost[] expectedPosts = buildAuditionPostsArray(10);
        Mockito.when(restTemplate.getForEntity(BASE_URL + "/posts", AuditionPost[].class))
            .thenReturn(ResponseEntity.ok(expectedPosts));

        final List<AuditionPost> posts = auditionIntegrationClient.getPosts();

        assertEquals(Arrays.asList(expectedPosts), posts);
    }

    @Test
    void shouldRetrievePostById() {
        final AuditionPost expectedPost = buildAuditionPost();
        Mockito.when(restTemplate.getForObject(POSTS_URL + POST_ID, AuditionPost.class))
            .thenReturn(expectedPost);

        final AuditionPost post = auditionIntegrationClient.getPostById(POST_ID);

        assertEquals(expectedPost, post);
    }

    @Test
    void shouldThrowSystemExceptionForNonExistentPost() {
        Mockito.when(restTemplate.getForObject(POSTS_URL + POST_ID, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final SystemException systemException = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(POST_ID));

        assertEquals("Cannot find a Post with id " + POST_ID, systemException.getDetail());
        assertEquals("Resource Not Found", systemException.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), systemException.getStatusCode());
    }

    @Test
    void shouldFetchCommentsForPost() {
        final PostComment[] expectedComments = buildPostCommentsArray(1, 7);
        Mockito.when(restTemplate.getForEntity(BASE_URL +  "/comments?postId=" + POST_ID, PostComment[].class))
            .thenReturn(ResponseEntity.ok(expectedComments));

        final List<PostComment> comments = auditionIntegrationClient.getCommentsByPostId(POST_ID);

        assertEquals(Arrays.asList(expectedComments), comments);
    }

    @Test
    void shouldRetrievePostWithAssociatedComments() {
        final AuditionPost expectedPost = buildAuditionPost();
        final PostComment[] expectedComments = buildPostCommentsArray(1, 7);
        expectedPost.setComments(Arrays.asList(expectedComments));

        Mockito.when(restTemplate.getForObject(POSTS_URL + POST_ID, AuditionPost.class))
            .thenReturn(expectedPost);
        Mockito.when(restTemplate.getForEntity(POSTS_URL + POST_ID + "/comments", PostComment[].class))
            .thenReturn(ResponseEntity.ok(expectedComments));

        final AuditionPost auditionPost = auditionIntegrationClient.getPostWithComments(POST_ID);
        assertEquals(expectedPost, auditionPost);
    }

    @Test
    void shouldThrowSystemExceptionForNonExistentPostWithComments() {
        Mockito.when(restTemplate.getForObject(POSTS_URL + POST_ID, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final SystemException systemException = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostWithComments(POST_ID));

        assertEquals("Cannot find a Post with id " + POST_ID, systemException.getDetail());
        assertEquals("Resource Not Found", systemException.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), systemException.getStatusCode());
    }
}