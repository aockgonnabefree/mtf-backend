package ku.cs.mtf_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateWp46Payload {

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^[A-Za-z]{1,2}[0-9]{6,7}$", message = "Passport number format is invalid")
    private String passportNo;

    @NotBlank(message = "Employer ID is required")
    @Size(min = 13, max = 13, message = "Employer ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Employer ID must contain only digits")
    private String employerId;

    @NotBlank(message = "Type of work is required")
    private String typeOfWork;

    @NotBlank(message = "Nature of work is required")
    private String natureOfWork;

    @NotNull(message = "Employment period year is required")
    @Min(value = 0, message = "Employment period year must be >= 0")
    private Integer periodOfEmploymentYear;

    @NotNull(message = "Employment period month is required")
    @Min(value = 0, message = "Employment period month must be >= 0")
    @Max(value = 11, message = "Employment period month must be <= 11")
    private Integer periodOfEmploymentMonth;

    @NotNull(message = "Employment period day is required")
    @Min(value = 0, message = "Employment period day must be >= 0")
    @Max(value = 30, message = "Employment period day must be <= 30")
    private Integer periodOfEmploymentDay;

    @NotBlank(message = "Employment valid until date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in YYYY-MM-DD format")
    private String employmentValidUntil;

    @NotNull(message = "Income per day is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Income per day must be > 0")
    private BigDecimal incomePerDay;

    @NotNull(message = "Benefit per day is required")
    @DecimalMin(value = "0.0", message = "Benefit per day must be >= 0")
    private BigDecimal benefitPerDay;

    @NotBlank(message = "Highest education is required")
    @Pattern(regexp = "ประถมศึกษา|มัธยมศึกษา|ปริญญาตรี|ปริญญาโท|ปริญญาเอก",
             message = "Highest education must be one of: ประถมศึกษา, มัธยมศึกษา, ปริญญาตรี, ปริญญาโท, ปริญญาเอก")
    private String highestEducation;

    @NotNull(message = "Work experience is required")
    @DecimalMin(value = "0.0", message = "Work experience must be >= 0")
    private BigDecimal workExperience;

    @NotBlank(message = "Reason for not employing Thai person is required")
    private String reasonForNotEmployingThaiPerson;

    public void setPassportNo(String passportNo) {
        this.passportNo = (passportNo == null) ? null : passportNo.strip();
    }

    public void setEmployerId(String employerId) {
        this.employerId = (employerId == null) ? null : employerId.strip();
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork = (typeOfWork == null) ? null : typeOfWork.strip();
    }

    public void setNatureOfWork(String natureOfWork) {
        this.natureOfWork = (natureOfWork == null) ? null : natureOfWork.strip();
    }

    public void setEmploymentValidUntil(String employmentValidUntil) {
        this.employmentValidUntil = (employmentValidUntil == null) ? null : employmentValidUntil.strip();
    }

    public void setHighestEducation(String highestEducation) {
        this.highestEducation = (highestEducation == null) ? null : highestEducation.strip();
    }

    public void setReasonForNotEmployingThaiPerson(String reasonForNotEmployingThaiPerson) {
        this.reasonForNotEmployingThaiPerson = (reasonForNotEmployingThaiPerson == null) ? null : reasonForNotEmployingThaiPerson.strip();
    }
}
