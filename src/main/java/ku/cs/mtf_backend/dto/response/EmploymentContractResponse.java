package ku.cs.mtf_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class EmploymentContractResponse {
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

    @JsonRawValue
    private String employeeSnapshot;  // String ไป JSON

    @JsonRawValue
    private String employerSnapshot;  // String ไป JSON
}
