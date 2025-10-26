package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateAdminPayload {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotNull(message = "Address is required")
    @Valid
    private CreateAddressPayload address;

    // Setters with strip()
    public void setFirstName(String firstName) {
        this.firstName = (firstName == null) ? null : firstName.strip();
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName == null) ? null : lastName.strip();
    }

    public void setEmail(String email) {
        this.email = (email == null) ? null : email.strip();
    }

    public void setPassword(String password) {
        this.password = (password == null) ? null : password.strip();
    }
}