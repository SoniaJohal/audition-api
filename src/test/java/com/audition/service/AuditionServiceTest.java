package com.audition.service;

import static com.audition.util.AuditionApiTestUtil.buildAuditionPost;
import static com.audition.util.AuditionApiTestUtil.buildAuditionPosts;
import static com.audition.util.AuditionApiTestUtil.buildPostComments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @Test
    void shouldFetchAllPosts() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        final List<AuditionPost> response = auditionService.getPosts(1, 25);

        assertEquals(expectedAuditionPosts, response);
    }

    @Test
    void shouldFetchRequestedNumberOfPosts() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        final List<AuditionPost> response = auditionService.getPosts(1, 10);

        assertEquals(10, response.size());
        assertEquals(1, response.get(0).getId());
        assertEquals(10, response.get(9).getId());
    }

    @Test
    void shouldFetchRequestedNumberOfPostsFromSelectedPage() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(25);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        final List<AuditionPost> response = auditionService.getPosts(2, 10);

        assertEquals(10, response.size());
        assertEquals(11, response.get(0).getId());
        assertEquals(20, response.get(9).getId());
    }


    @Test
    void shouldFetchFilteredPosts() {
        final String title = "repellat";
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(buildAuditionPosts(5));

        final List<AuditionPost> response = auditionService.getPostsFilteredByTitle(title, 1, 10);

        assertEquals(5, response.size());
        assertTrue(response.get(0).getTitle().contains(title));
        assertTrue(response.get(4).getTitle().contains(title));
    }

    @Test
    void shouldReturnAllPostsWhenPageSizeExceedsTotalPosts() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(10);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        final List<AuditionPost> response = auditionService.getPosts(1, 20);

        assertEquals(10, response.size());
        assertEquals(1, response.get(0).getId());
        assertEquals(10, response.get(9).getId());
    }

    @Test
    void shouldReturnLastPagePostsWhenPageSizeExceedsTotalPosts() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(10);
        Mockito.when(auditionIntegrationClient.getPosts()).thenReturn(expectedAuditionPosts);

        final List<AuditionPost> response = auditionService.getPosts(5, 5);

        assertEquals(5, response.size());
        assertEquals(6, response.get(0).getId());
        assertEquals(10, response.get(4).getId());
    }

    @Test
    void shouldRetrievePostById() {
        final AuditionPost expectedAuditionPost = buildAuditionPost();
        Mockito.when(auditionIntegrationClient.getPostById("1")).thenReturn(expectedAuditionPost);

        final AuditionPost response = auditionService.getPostById("1");

        assertEquals(expectedAuditionPost, response);
    }

    @Test
    void shouldRetrievePostWithComments() {
        final List<AuditionPost> expectedAuditionPosts = buildAuditionPosts(1);
        Mockito.when(auditionIntegrationClient.getPostWithComments("1")).thenReturn(expectedAuditionPosts.get(0));

        final AuditionPost response = auditionService.getPostWithComments("1");

        assertEquals(1, response.getId());
    }

    @Test
    void shouldRetrieveCommentsByPostId() {
        final List<PostComment> expectedComments = buildPostComments(1, 6);
        Mockito.when(auditionIntegrationClient.getCommentsByPostId("1")).thenReturn(expectedComments);

        final List<PostComment> response = auditionService.getCommentsByPostId("1");

        assertEquals(expectedComments, response);
    }
}