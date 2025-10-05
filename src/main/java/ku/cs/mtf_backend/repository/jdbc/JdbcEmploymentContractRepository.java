package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.EmploymentContract;
import ku.cs.mtf_backend.repository.EmploymentContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmploymentContractRepository implements EmploymentContractRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcEmploymentContractRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public EmploymentContract save(EmploymentContract contract) {
        String sql = """
            INSERT INTO EMPLOYMENT_CONTRACT (
                Id, Type_of_work_th, Type_of_work_en,
                Income_per_day, Paid_income_at, Working_hour_limit,
                Working_day_per_week, Employment_period_month,
                Day_off_weekly_th, Day_off_weekly_en,
                Day_off_holiday_th, Day_off_holiday_en,
                Days_annual_leave_th, Days_annual_leave_en,
                Overtime_rate_th, Overtime_rate_en,
                Holiday_overtime_rate_th, Holiday_overtime_rate_en,
                Created_at, Employee_snapshot, Employer_snapshot,
                Employer_id, Employee_id
            ) VALUES (
                :id, :typeOfWorkTh, :typeOfWorkEn,
                :incomePerDay, :paidIncomeAt, :workingHourLimit,
                :workingDayPerWeek, :employmentPeriodMonth,
                :dayOffWeeklyTh, :dayOffWeeklyEn,
                :dayOffHolidayTh, :dayOffHolidayEn,
                :daysAnnualLeaveTh, :daysAnnualLeaveEn,
                :overtimeRateTh, :overtimeRateEn,
                :holidayOvertimeRateTh, :holidayOvertimeRateEn,
                :createdAt, CAST(:employeeSnapshot AS JSONB), CAST(:employerSnapshot AS JSONB),
                :employerId, :employeeId
            )
            """;

        jdbcClient.sql(sql)
                .param("id", contract.getId())
                .param("typeOfWorkTh", contract.getTypeOfWorkTh())
                .param("typeOfWorkEn", contract.getTypeOfWorkEn())
                .param("incomePerDay", contract.getIncomePerDay())
                .param("paidIncomeAt", contract.getPaidIncomeAt())
                .param("workingHourLimit", contract.getWorkingHourLimit())
                .param("workingDayPerWeek", contract.getWorkingDayPerWeek())
                .param("employmentPeriodMonth", contract.getEmploymentPeriodMonth())
                .param("dayOffWeeklyTh", contract.getDayOffWeeklyTh())
                .param("dayOffWeeklyEn", contract.getDayOffWeeklyEn())
                .param("dayOffHolidayTh", contract.getDayOffHolidayTh())
                .param("dayOffHolidayEn", contract.getDayOffHolidayEn())
                .param("daysAnnualLeaveTh", contract.getDaysAnnualLeaveTh())
                .param("daysAnnualLeaveEn", contract.getDaysAnnualLeaveEn())
                .param("overtimeRateTh", contract.getOvertimeRateTh())
                .param("overtimeRateEn", contract.getOvertimeRateEn())
                .param("holidayOvertimeRateTh", contract.getHolidayOvertimeRateTh())
                .param("holidayOvertimeRateEn", contract.getHolidayOvertimeRateEn())
                .param("createdAt", contract.getCreatedAt())
                .param("employeeSnapshot", contract.getEmployeeSnapshot())
                .param("employerSnapshot", contract.getEmployerSnapshot())
                .param("employerId", contract.getEmployerId())
                .param("employeeId", contract.getEmployeeId())
                .update();

        return contract;
    }

    @Override
    public Optional<EmploymentContract> findById(String id) {
        String sql = "SELECT * FROM EMPLOYMENT_CONTRACT WHERE Id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(this::mapRowToEmploymentContract)
                .optional();
    }

    @Override
    public List<EmploymentContract> findLatestByEmployeeAndEmployer(String employeeId, String employerId, int limit) {
        String sql = """
            SELECT * FROM EMPLOYMENT_CONTRACT
            WHERE Employee_id = :employeeId AND Employer_id = :employerId
            ORDER BY Created_at DESC
            LIMIT :limit
            """;

        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .param("employerId", employerId)
                .param("limit", limit)
                .query(this::mapRowToEmploymentContract)
                .list();
    }

    @Override
    public List<EmploymentContract> findLatestByEmployeeId(String employeeId, int limit) {
        String sql = """
            SELECT * FROM EMPLOYMENT_CONTRACT
            WHERE Employee_id = :employeeId
            ORDER BY Created_at DESC
            LIMIT :limit
            """;

        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .param("limit", limit)
                .query(this::mapRowToEmploymentContract)
                .list();
    }

    private EmploymentContract mapRowToEmploymentContract(ResultSet rs, int rowNum) throws SQLException {
        return EmploymentContract.builder()
                .id(rs.getString("Id"))
                .typeOfWorkTh(rs.getString("Type_of_work_th"))
                .typeOfWorkEn(rs.getString("Type_of_work_en"))
                .incomePerDay(rs.getBigDecimal("Income_per_day"))
                .paidIncomeAt(rs.getInt("Paid_income_at"))
                .workingHourLimit(rs.getInt("Working_hour_limit"))
                .workingDayPerWeek(rs.getInt("Working_day_per_week"))
                .employmentPeriodMonth(rs.getInt("Employment_period_month"))
                .dayOffWeeklyTh(rs.getString("Day_off_weekly_th"))
                .dayOffWeeklyEn(rs.getString("Day_off_weekly_en"))
                .dayOffHolidayTh(rs.getString("Day_off_holiday_th"))
                .dayOffHolidayEn(rs.getString("Day_off_holiday_en"))
                .daysAnnualLeaveTh(rs.getString("Days_annual_leave_th"))
                .daysAnnualLeaveEn(rs.getString("Days_annual_leave_en"))
                .overtimeRateTh(rs.getString("Overtime_rate_th"))
                .overtimeRateEn(rs.getString("Overtime_rate_en"))
                .holidayOvertimeRateTh(rs.getString("Holiday_overtime_rate_th"))
                .holidayOvertimeRateEn(rs.getString("Holiday_overtime_rate_en"))
                .createdAt(rs.getTimestamp("Created_at").toLocalDateTime())
                .employeeSnapshot(rs.getString("Employee_snapshot"))
                .employerSnapshot(rs.getString("Employer_snapshot"))
                .employerId(rs.getString("Employer_id"))
                .employeeId(rs.getString("Employee_id"))
                .build();
    }
}
