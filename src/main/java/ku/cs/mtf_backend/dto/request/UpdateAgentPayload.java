package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAgentPayload {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be 'ACTIVE' or 'INACTIVE'")
    private String status;

    
    @Valid
    private CreateAddressPayload address;

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
