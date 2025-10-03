package ku.cs.mtf_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateEmployerPayload {
    @NotBlank(message = "ID is required")
    @Size(min = 13, max = 13, message = "ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "ID must contain only digits")
    String id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "First name is required")
    String firstName;

    @NotBlank(message = "Last name is required")
    String lastName;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be 'ACTIVE' or 'INACTIVE'")
    String status;

    String companyName;

    @NotBlank(message = "Business type is required")
    String businessType;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    String phoneNumber;

    @NotNull(message = "Financial year is required")
    @Min(value = 1900, message = "Financial year must be valid")
    Integer financialStatusYear;

    @NotNull(message = "Financial income is required")
    @PositiveOrZero(message = "Financial income cannot be negative")
    Double financialStatusIncome;

    @NotNull(message = "Financial tax is required")
    @PositiveOrZero(message = "Financial tax cannot be negative")
    Double financialStatusTax;

    @NotNull(message = "Current income is required")
    @PositiveOrZero(message = "Current income cannot be negative")
    Double currentIncome;

    @NotNull(message = "Income duration is required")
    @Positive(message = "Income duration must be positive")
    Integer incomeDuration;

    @NotNull(message = "Address is required")
    @Valid
    CreateAddressPayload address;
}
