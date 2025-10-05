package ku.cs.mtf_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmploymentContractPayload {

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^[A-Za-z]{1,2}[0-9]{6,7}$", message = "Passport number format is invalid")
    private String passportNo;

    @NotBlank(message = "Employer ID is required")
    @Size(min = 13, max = 13, message = "Employer ID must be 13 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Employer ID must contain only digits")
    private String employerId;

    @NotBlank(message = "Type of work (TH) is required")
    private String typeOfWorkTh;

    @NotBlank(message = "Type of work (EN) is required")
    private String typeOfWorkEn;

    @NotNull(message = "Income per day is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Income per day must be > 0")
    private BigDecimal incomePerDay;

    @NotNull(message = "Paid income at (day of month) is required")
    @Min(value = 1, message = "Paid income at must be >= 1")
    @Max(value = 31, message = "Paid income at must be <= 31")
    private Integer paidIncomeAt;

    @NotNull(message = "Employment period month is required")
    @Min(value = 1, message = "Employment period month must be >= 1")
    private Integer employmentPeriodMonth;

    @NotNull(message = "Working hour limit is required")
    @Min(value = 1, message = "Working hour limit must be >= 1")
    @Max(value = 24, message = "Working hour limit must be <= 24")
    private Integer workingHourLimit;

    @NotNull(message = "Working day per week is required")
    @Min(value = 1, message = "Working day per week must be >= 1")
    @Max(value = 7, message = "Working day per week must be <= 7")
    private Integer workingDayPerWeek;

    @NotBlank(message = "Day off weekly (TH) is required")
    private String dayOffWeeklyTh;

    @NotBlank(message = "Day off weekly (EN) is required")
    private String dayOffWeeklyEn;

    @NotBlank(message = "Day off holiday (TH) is required")
    private String dayOffHolidayTh;

    @NotBlank(message = "Day off holiday (EN) is required")
    private String dayOffHolidayEn;

    @NotBlank(message = "Days annual leave (TH) is required")
    private String daysAnnualLeaveTh;

    @NotBlank(message = "Days annual leave (EN) is required")
    private String daysAnnualLeaveEn;

    @NotBlank(message = "Overtime rate (TH) is required")
    private String overtimeRateTh;

    @NotBlank(message = "Overtime rate (EN) is required")
    private String overtimeRateEn;

    @NotBlank(message = "Holiday overtime rate (TH) is required")
    private String holidayOvertimeRateTh;

    @NotBlank(message = "Holiday overtime rate (EN) is required")
    private String holidayOvertimeRateEn;

    public void setPassportNo(String passportNo) {
        this.passportNo = (passportNo == null) ? null : passportNo.strip();
    }

    public void setEmployerId(String employerId) {
        this.employerId = (employerId == null) ? null : employerId.strip();
    }

    public void setTypeOfWorkTh(String typeOfWorkTh) {
        this.typeOfWorkTh = (typeOfWorkTh == null) ? null : typeOfWorkTh.strip();
    }

    public void setTypeOfWorkEn(String typeOfWorkEn) {
        this.typeOfWorkEn = (typeOfWorkEn == null) ? null : typeOfWorkEn.strip();
    }

    public void setDayOffWeeklyTh(String dayOffWeeklyTh) {
        this.dayOffWeeklyTh = (dayOffWeeklyTh == null) ? null : dayOffWeeklyTh.strip();
    }

    public void setDayOffWeeklyEn(String dayOffWeeklyEn) {
        this.dayOffWeeklyEn = (dayOffWeeklyEn == null) ? null : dayOffWeeklyEn.strip();
    }

    public void setDayOffHolidayTh(String dayOffHolidayTh) {
        this.dayOffHolidayTh = (dayOffHolidayTh == null) ? null : dayOffHolidayTh.strip();
    }

    public void setDayOffHolidayEn(String dayOffHolidayEn) {
        this.dayOffHolidayEn = (dayOffHolidayEn == null) ? null : dayOffHolidayEn.strip();
    }

    public void setDaysAnnualLeaveTh(String daysAnnualLeaveTh) {
        this.daysAnnualLeaveTh = (daysAnnualLeaveTh == null) ? null : daysAnnualLeaveTh.strip();
    }

    public void setDaysAnnualLeaveEn(String daysAnnualLeaveEn) {
        this.daysAnnualLeaveEn = (daysAnnualLeaveEn == null) ? null : daysAnnualLeaveEn.strip();
    }

    public void setOvertimeRateTh(String overtimeRateTh) {
        this.overtimeRateTh = (overtimeRateTh == null) ? null : overtimeRateTh.strip();
    }

    public void setOvertimeRateEn(String overtimeRateEn) {
        this.overtimeRateEn = (overtimeRateEn == null) ? null : overtimeRateEn.strip();
    }

    public void setHolidayOvertimeRateTh(String holidayOvertimeRateTh) {
        this.holidayOvertimeRateTh = (holidayOvertimeRateTh == null) ? null : holidayOvertimeRateTh.strip();
    }

    public void setHolidayOvertimeRateEn(String holidayOvertimeRateEn) {
        this.holidayOvertimeRateEn = (holidayOvertimeRateEn == null) ? null : holidayOvertimeRateEn.strip();
    }
}
