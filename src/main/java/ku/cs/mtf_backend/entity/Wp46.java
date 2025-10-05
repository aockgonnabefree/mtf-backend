package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Table("WP_46")
public class Wp46 {

    @Id
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
    private String employeeSnapshot;  // JSONB stored as String
    private String employerSnapshot;  // JSONB stored as String

    private String employerId;
    private String employeeId;
}
