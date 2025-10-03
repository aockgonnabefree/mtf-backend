package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmployeePayload {

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^[A-Za-z]{1,2}[0-9]{6,7}$", message = "Passport number format is invalid (e.g., A1234567 or AB123456)")
    private String passportNo;

    @NotBlank(message = "Employer ID is required")
    @Size(min = 13, max = 13, message = "Employer ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Employer ID must contain only digits")
    private String employerId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be 'active' or 'inactive'")
    private String status;

    @NotBlank(message = "Nationality is required")
    @Pattern(regexp = "เมียนมา|กัมพูชา|ลาว", message = "Nationality must be 'เมียนมา', 'กัมพูชา', or 'ลาว'")
    private String nationality;

    @NotBlank(message = "Blood type is required")
    @Pattern(regexp = "A|B|AB|O", message = "Blood type must be 'A', 'B', 'AB', or 'O'")
    private String bloodType;

    @NotNull(message = "Address is required")
    @Valid
    private CreateAddressPayload address;

    @NotNull(message = "Documents list cannot be null (can be an empty list)")
    @Valid
    private List<DocumentPayload> documents;

    public void setPassportNo(String passportNo) {
        this.passportNo = (passportNo == null) ? null : passportNo.strip();
    }

    public void setEmployerId(String employerId) {
        this.employerId = (employerId == null) ? null : employerId.strip();
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName == null) ? null : firstName.strip();
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName == null) ? null : lastName.strip();
    }

    public void setStatus(String status) {
        this.status = (status == null) ? null : status.strip();
    }

    public void setNationality(String nationality) {
        this.nationality = (nationality == null) ? null : nationality.strip();
    }

    public void setBloodType(String bloodType) {
        this.bloodType = (bloodType == null) ? null : bloodType.strip();
    }
}

