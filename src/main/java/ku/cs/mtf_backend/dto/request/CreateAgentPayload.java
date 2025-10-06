package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateAgentPayload {

    @NotBlank(message = "ID is required")
    @Size(min = 13, max = 13, message = "ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "ID must contain only digits")
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be 'ACTIVE' or 'INACTIVE'")
    private String status;

    @NotNull(message = "Address is required")
    @Valid
    private CreateAddressPayload address;

    // Setters with strip()
    public void setId(String id) {
        this.id = (id == null) ? null : id.strip();
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName == null) ? null : firstName.strip();
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName == null) ? null : lastName.strip();
    }

    public void setEmail(String email) {
        this.email = (email == null) ? null : email.strip();
    }

    public void setStatus(String status) {
        this.status = (status == null) ? null : status.strip();
    }
}
