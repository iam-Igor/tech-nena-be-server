package isuruygor.demo.responses;

import isuruygor.demo.entities.Role;

public record UserResponse(
        String avatar,
        String email,
        Long id,
        String lastname,
        String name,
        Role role,
        String username
) {
}
