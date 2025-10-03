package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@Table("EMPLOYER")
public class Employer {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String businessType;
    private Integer financialStatusYear;
    private BigDecimal financialStatusIncome;
    private BigDecimal financialStatusTax;
    private BigDecimal currentIncome;
    private Integer incomeDuration;
    private String status;
    private String companyName;
    private String addressId;
}
