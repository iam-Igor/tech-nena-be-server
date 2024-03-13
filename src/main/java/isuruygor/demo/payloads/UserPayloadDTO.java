package isuruygor.demo.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserPayloadDTO(

        @NotEmpty(message = "Campo obbligatorio per la proprietà name.")
        String name,
        @NotEmpty(message = "Campo obbligatorio per la proprietà lastname.")
        String lastname,
        @NotEmpty(message = "Campo obbligatorio per la proprietà username.")
        String username,
        @NotEmpty(message = "Campo obbligatorio per la proprietà email.")
        String email,
        @NotEmpty(message = "Campo obbligatorio per la proprietà password.")
        @Size(min = 6, message = "La password deve avere una lunghezza minima di 6 caratteri.")
        String password

) {
}
