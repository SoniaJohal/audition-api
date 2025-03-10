package com.audition.util;

import com.audition.model.AuditionPost;
import com.audition.model.PostComment;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuditionApiTestUtil {

    public static List<AuditionPost> buildAuditionPosts(final int size) {
        return IntStream.rangeClosed(1, size)
            .mapToObj(i -> AuditionPost.builder()
                .id(i)
                .title("Post with title repellat" + i)
                .body("Body of " + i + "th post")
                .userId(999)
                .comments(Collections.emptyList())
                .build())
            .collect(Collectors.toList());
    }

    public static AuditionPost[] buildAuditionPostsArray(final int size) {
        return buildAuditionPosts(size).toArray(new AuditionPost[0]);
        }

    public static List<PostComment> buildPostComments(final int postId, final int size) {
        return IntStream.rangeClosed(1, size)
            .mapToObj(i -> PostComment.builder()
                .id(i)
                .name("Akash")
                .postId(postId)
                .body("Comment no. " + i)
                .email("test@medi.com")
                .build())
            .collect(Collectors.toList());
    }

    public static PostComment[] buildPostCommentsArray(final int postId, final int size) {
        return buildPostComments(postId, size).toArray(new PostComment[0]);
    }

    public static AuditionPost buildAuditionPost() {
        return buildAuditionPosts(1).get(0);
    }

}
