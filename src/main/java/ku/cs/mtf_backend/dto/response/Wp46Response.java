package ku.cs.mtf_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Wp46Response {
    private String id;
    private String typeOfWork;
    private String natureOfWork;
    private Integer periodOfEmploymentYear;
    private Integer periodOfEmploymentMonth;
    private Integer periodOfEmploymentDay;
    private LocalDate employmentValidUntil;
    private BigDecimal incomePerDay;
    private BigDecimal benefitPerDay;
    private String highestEducation;
    private BigDecimal workExperience;
    private String reasonForNotEmployingThaiPerson;
    private LocalDateTime createdAt;

    @JsonRawValue
    private String employeeSnapshot;  // String ไป JSON

    @JsonRawValue
    private String employerSnapshot;  // String ไป JSON
}
