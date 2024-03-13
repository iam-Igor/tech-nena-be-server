package isuruygor.demo.payloads;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record PostPayloadDTO(

        @NotEmpty
        String title,
        @NotEmpty
        String name,
        @NotEmpty
        String category,
        @NotEmpty
        String content,

        @NotEmpty
        Set<String> tags


) {
}
