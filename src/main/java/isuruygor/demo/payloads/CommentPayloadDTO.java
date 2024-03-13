package isuruygor.demo.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CommentPayloadDTO(
        @NotEmpty(message = "comment can't be empty")
        @Size(max = 500)
        String comment
) {
}


