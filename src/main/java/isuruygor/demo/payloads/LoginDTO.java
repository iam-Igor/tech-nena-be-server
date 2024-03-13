package isuruygor.demo.payloads;

import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(@NotEmpty(message = "Campo obbligatorio per la proprietà email.")
                       String email,

                       @NotEmpty(message = "Campo obbligatorio per la proprietà password.")
                       String password) {
}
