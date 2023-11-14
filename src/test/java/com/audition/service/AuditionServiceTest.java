package com.audition.service;

import static com.audition.util.AuditionApiTestUtil.buildAuditionPost;
import static com.audition.util.AuditionApiTestUtil.buildAuditionPosts;
import static com.audition.util.AuditionApiTestUtil.buildPostComments;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @Test
    public void getPostsShouldReturnAllPosts() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        List<AuditionPost> response = auditionService.getPosts(1, 25);

        assertEquals(expectedAuditionPosts, response);
    }

    @Test
    public void getPostsShouldReturnRequestedNumberOfPosts() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        List<AuditionPost> response = auditionService.getPosts(1, 10);

        assertEquals(10, response.size());
        assertEquals(1, response.get(0).getId());
        assertEquals(10, response.get(9).getId());
    }

    @Test
    public void getPostsShouldReturnRequestedNumberOfPostsFromSelectedPage() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        List<AuditionPost> response = auditionService.getPosts(2, 10);

        assertEquals(10, response.size());
        assertEquals(11, response.get(0).getId());
        assertEquals(20, response.get(9).getId());
    }

    @Test
    public void getPostsShouldReturnAllPostsWhenPageSizeIsGreaterThanPosts() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(10);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        List<AuditionPost> response = auditionService.getPosts(1, 20);

        assertEquals(10, response.size());
        assertEquals(1, response.get(0).getId());
        assertEquals(10, response.get(9).getId());
    }

    @Test
    public void getPostsShouldReturnLastPagePostsWhenPageSizeIsGreaterThanPosts() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(10);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        List<AuditionPost> response = auditionService.getPosts(5, 5);

        assertEquals(5, response.size());
        assertEquals(6, response.get(0).getId());
        assertEquals(10, response.get(4).getId());
    }

    @Test
    public void getPostByIdShouldReturnPost() {
        final AuditionPost expectedAuditionPost = buildAuditionPost();
        Mockito.when(auditionIntegrationClient.getPostById("1")).thenReturn(expectedAuditionPost);
        AuditionPost response = auditionService.getPostById("1");
        assertEquals(expectedAuditionPost, response);
    }

    @Test
    public void getPostWithCommentsShouldReturnPostPopulatedWithComments() {
        List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(1);
        Mockito.when(auditionIntegrationClient.getPostWithComments("1")).thenReturn(expectedAuditionPosts.get(0));

        AuditionPost response = auditionService.getPostWithComments("1");

        assertEquals(1, response.getId());
    }

    @Test
    public void getCommentsByPostIdShouldReturnCommentsForThePost() {
        List<PostComment> expectedComments = buildPostComments(1, 6);
        Mockito.when(auditionIntegrationClient.getCommentsByPostId("1")).thenReturn(expectedComments);

        final List<PostComment> response = auditionService.getCommentsByPostId("1");

        assertEquals(expectedComments, response);
    }
}