package isuruygor.demo.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record UserUpdateDTO(
        String name,
        String lastname,
        String username,
        String email,
//        @Nullable
        @Size(min = 6, message = "La password deve avere una lunghezza minima di 6 caratteri.")
        String password

) {
}
