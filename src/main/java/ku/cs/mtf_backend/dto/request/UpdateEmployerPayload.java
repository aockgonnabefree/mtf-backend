package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEmployerPayload {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be 'ACTIVE' or 'INACTIVE'")
    private String status;

    private String companyName;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    private String phoneNumber;

    @NotNull(message = "Financial year is required")
    @Min(value = 1900, message = "Financial year must be valid")
    private Integer financialStatusYear;

    @NotNull(message = "Financial income is required")
    @PositiveOrZero(message = "Financial income cannot be negative")
    private Double financialStatusIncome;

    @NotNull(message = "Financial tax is required")
    @PositiveOrZero(message = "Financial tax cannot be negative")
    private Double financialStatusTax;

    @NotNull(message = "Current income is required")
    @PositiveOrZero(message = "Current income cannot be negative")
    private Double currentIncome;

    @NotNull(message = "Income duration is required")
    @Positive(message = "Income duration must be positive")
    private Integer incomeDuration;

    @NotNull(message = "Address is required")
    @Valid
    private CreateAddressPayload address;

    public void setEmail(String email) {
        this.email = (email == null) ? null : email.strip();
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName == null) ? null : firstName.strip();
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName == null) ? null : lastName.strip();
    }

    public void setCompanyName(String companyName) {
        this.companyName = (companyName == null) ? null : companyName.strip();
    }

    public void setBusinessType(String businessType) {
        this.businessType = (businessType == null) ? null : businessType.strip();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber == null) ? null : phoneNumber.strip();
    }
}
