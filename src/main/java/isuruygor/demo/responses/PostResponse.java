package isuruygor.demo.responses;

import isuruygor.demo.entities.PostCategory;
import isuruygor.demo.entities.PostType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime postData,
        PostCategory category,
        Set<String> postTags,
        String postImage,
        Boolean approved,
        PostType state,
        String username,
        String name,
        String lastname,
        String avatarUrl,
        Long userId
) {
}
