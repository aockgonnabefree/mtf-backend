package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Table("EMPLOYMENT_CONTRACT")
public class EmploymentContract {

    @Id
    private String id;

    private String typeOfWorkTh;
    private String typeOfWorkEn;
    private BigDecimal incomePerDay;
    private Integer paidIncomeAt;
    private Integer workingHourLimit;
    private Integer workingDayPerWeek;
    private Integer employmentPeriodMonth;
    private String dayOffWeeklyTh;
    private String dayOffWeeklyEn;
    private String dayOffHolidayTh;
    private String dayOffHolidayEn;
    private String daysAnnualLeaveTh;
    private String daysAnnualLeaveEn;
    private String overtimeRateTh;
    private String overtimeRateEn;
    private String holidayOvertimeRateTh;
    private String holidayOvertimeRateEn;
    private LocalDateTime createdAt;
    private String employeeSnapshot;  // JSONB stored as String
    private String employerSnapshot;  // JSONB stored as String

    private String employerId;
    private String employeeId;
}
