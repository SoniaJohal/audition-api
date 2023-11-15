package com.audition.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.http.ResponseEntity;

public class AuditionApiTestUtil {

    public static List<AuditionPost> buildAuditionPosts(final int size) {
        return IntStream.rangeClosed(1, size)
            .mapToObj(i -> AuditionPost.builder()
                .id(i)
                .title("Post with title repellat" + i)
                .body("Body of " + i + "th post")
                .userId(999)
                .build())
            .collect(Collectors.toList());
    }

    public static AuditionPost[] buildAuditionPostsArray(final int size) {
            return IntStream.rangeClosed(1, size)
                .mapToObj(i -> AuditionPost.builder()
                    .id(i)
                    .title("Post with title repellat" + i)
                    .body("Body of " + i + "th post")
                    .userId(999)
                    .build())
                .toArray(AuditionPost[]::new);
        }

    public static List<PostComment> buildPostComments(final int postId, final int size) {
        return IntStream.rangeClosed(1, size)
            .mapToObj(i -> PostComment.builder()
                .id(i)
                .postId(postId)
                .body("Comment no. " + i)
                .email("test@medi.com")
                .build())
            .collect(Collectors.toList());
    }

    public static PostComment[] buildPostCommentsArray(final int postId, final int size) {
        return IntStream.rangeClosed(1, size)
            .mapToObj(i -> PostComment.builder()
                .id(i)
                .postId(postId)
                .body("Comment no. " + i)
                .email("test@medi.com")
                .build())
            .toArray(PostComment[]::new);
    }

    public static AuditionPost buildAuditionPost() {
        return buildAuditionPosts(1).get(0);
    }

    public static String buildExpectedErrorResponse(final String title, final String message, final String instance, final int statusCode) {
        return String.format("{\"type\":\"about:blank\",\"title\":\"%s\",\"status\":%d,\"detail\":\"%s\",\"instance\":\"/%s\"}",
            title, statusCode, message, instance);
    }

    public static void verifyTraceAndSpanIDExists(final ResponseEntity response) {
        assertNotNull(response.getHeaders().get("X-Trace-Id"));
        assertNotNull(response.getHeaders().get("X-Span-Id"));
    }

}
