package com.audition;

import static com.audition.util.AuditionApiTestUtil.buildExpectedErrorResponse;
import static com.audition.util.AuditionApiTestUtil.verifyTraceAndSpanIDExists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.configuration.SleuthConfig;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SleuthConfig.class)
@SuppressWarnings("PMD.TooManyMethods")
class AuditionApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String postsBaseUrl;

    private static final String BASE_URL = "http://localhost:";

    @BeforeEach
    public void setup() {
        postsBaseUrl = BASE_URL + port + "/posts";
    }

    @Test
    void contextLoads() {
        assertNotNull(restTemplate);
    }

    @Test
    void shouldReturnFirstTenPosts() {
       final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(
            postsBaseUrl, AuditionPost[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(10, response.getBody().length);

        verifyTraceAndSpanIDExists(response);
    }

    @Test
    void shouldReturnPostsForSelectedPage() {
       final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(
            postsBaseUrl + "?page=2&pageSize=5", AuditionPost[].class);

        final AuditionPost[] auditionPosts = response.getBody();
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(5, auditionPosts.length);
        assertEquals(6, auditionPosts[0].getId());
        assertEquals(10, auditionPosts[4].getId());

        verifyTraceAndSpanIDExists(response);
    }

    @Test
    void shouldReturnErrorWhenInvalidPaginationParamsProvided() {
       final ResponseEntity<String> response = restTemplate.getForEntity(
            postsBaseUrl + "?page=0&pageSize=5", String.class);
        final String expectedResponse =  buildExpectedErrorResponse("API Error Occurred",
            "getPosts.page: must be greater than or equal to 1", "posts", HttpStatus.BAD_REQUEST.value());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void shouldReturnErrorWhenPageSizeExceedsMaxProvided() {
       final ResponseEntity<String> response = restTemplate.getForEntity(
            postsBaseUrl + "?page=1&pageSize=35", String.class);
        final String expectedResponse =  buildExpectedErrorResponse("API Error Occurred",
            "getPosts.pageSize: Page size cannot be greater than 25", "posts", HttpStatus.BAD_REQUEST.value());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void shouldFetchFilteredPosts() {
        final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(
            postsBaseUrl + "?filter=repellat", AuditionPost[].class);

        final AuditionPost[] auditionPosts = response.getBody();
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(2, auditionPosts.length);
        assertTrue(auditionPosts[0].getTitle().contains("repellat"));
        assertTrue(auditionPosts[1].getTitle().contains("repellat"));

        verifyTraceAndSpanIDExists(response);
    }

    @Test
    void shouldReturnPostsForSpecificId() {
      final  ResponseEntity<AuditionPost> response = restTemplate.getForEntity(
            postsBaseUrl + "/1", AuditionPost.class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void shouldReturnErrorWithInvalidPostId() {
       final ResponseEntity<String> response = restTemplate.getForEntity(
            postsBaseUrl + "/abc", String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertTrue(response.getBody().contains("getPostById.postId: postId must be a valid integer"));
        assertTrue(response.getBody().contains("getPostById.postId: must be greater than or equal to 1"));
    }

    @Test
    void shouldReturnErrorWhenPostNotFound() {
      final  ResponseEntity<String> response = restTemplate.getForEntity(
            postsBaseUrl + "/20000", String.class);

        final String expectedResponse =  buildExpectedErrorResponse("Resource Not Found",
            "Cannot find a Post with id 20000", "posts/20000", HttpStatus.NOT_FOUND.value());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void shouldReturnCommentsForSpecificPostId() {
       final ResponseEntity<PostComment[]> response = restTemplate.getForEntity(
            BASE_URL + port  + "/comments?postId=1", PostComment[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(5, response.getBody().length);

        verifyTraceAndSpanIDExists(response);
    }

    @Test
    void shouldReturnPostWithCommentsForSpecificPostId() {
       final ResponseEntity<AuditionPost> response = restTemplate.getForEntity(
            BASE_URL + port  + "/posts/1/comments", AuditionPost.class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(1, response.getBody().getId());
        assertEquals(5, response.getBody().getComments().size());

        verifyTraceAndSpanIDExists(response);
    }

}
