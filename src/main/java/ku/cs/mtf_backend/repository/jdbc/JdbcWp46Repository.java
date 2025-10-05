package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Wp46;
import ku.cs.mtf_backend.repository.Wp46Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcWp46Repository implements Wp46Repository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcWp46Repository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Wp46 save(Wp46 wp46) {
        String sql = """
            INSERT INTO WP_46 (
                Id, Type_of_work, Nature_of_work,
                Period_of_employment_year, Period_of_employment_month, Period_of_employment_day,
                Employment_valid_until, Income_per_day, Benefit_per_day,
                Highest_education, Work_experience, Reason_for_not_employing_thai_person,
                Created_at, Employee_snapshot, Employer_snapshot,
                Employer_id, Employee_id
            ) VALUES (
                :id, :typeOfWork, :natureOfWork,
                :periodOfEmploymentYear, :periodOfEmploymentMonth, :periodOfEmploymentDay,
                :employmentValidUntil, :incomePerDay, :benefitPerDay,
                CAST(:highestEducation AS highest_education), :workExperience, :reasonForNotEmployingThaiPerson,
                :createdAt, CAST(:employeeSnapshot AS JSONB), CAST(:employerSnapshot AS JSONB),
                :employerId, :employeeId
            )
            """;

        jdbcClient.sql(sql)
                .param("id", wp46.getId())
                .param("typeOfWork", wp46.getTypeOfWork())
                .param("natureOfWork", wp46.getNatureOfWork())
                .param("periodOfEmploymentYear", wp46.getPeriodOfEmploymentYear())
                .param("periodOfEmploymentMonth", wp46.getPeriodOfEmploymentMonth())
                .param("periodOfEmploymentDay", wp46.getPeriodOfEmploymentDay())
                .param("employmentValidUntil", wp46.getEmploymentValidUntil())
                .param("incomePerDay", wp46.getIncomePerDay())
                .param("benefitPerDay", wp46.getBenefitPerDay())
                .param("highestEducation", wp46.getHighestEducation())
                .param("workExperience", wp46.getWorkExperience())
                .param("reasonForNotEmployingThaiPerson", wp46.getReasonForNotEmployingThaiPerson())
                .param("createdAt", wp46.getCreatedAt())
                .param("employeeSnapshot", wp46.getEmployeeSnapshot())
                .param("employerSnapshot", wp46.getEmployerSnapshot())
                .param("employerId", wp46.getEmployerId())
                .param("employeeId", wp46.getEmployeeId())
                .update();

        return wp46;
    }

    @Override
    public Optional<Wp46> findById(String id) {
        String sql = "SELECT * FROM WP_46 WHERE Id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(this::mapRowToWp46)
                .optional();
    }

    @Override
    public List<Wp46> findLatestByEmployeeAndEmployer(String employeeId, String employerId, int limit) {
        String sql = """
            SELECT * FROM WP_46
            WHERE Employee_id = :employeeId AND Employer_id = :employerId
            ORDER BY Created_at DESC
            LIMIT :limit
            """;

        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .param("employerId", employerId)
                .param("limit", limit)
                .query(this::mapRowToWp46)
                .list();
    }

    @Override
    public List<Wp46> findLatestByEmployeeId(String employeeId, int limit) {
        String sql = """
            SELECT * FROM WP_46
            WHERE Employee_id = :employeeId
            ORDER BY Created_at DESC
            LIMIT :limit
            """;

        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .param("limit", limit)
                .query(this::mapRowToWp46)
                .list();
    }

    private Wp46 mapRowToWp46(ResultSet rs, int rowNum) throws SQLException {
        return Wp46.builder()
                .id(rs.getString("Id"))
                .typeOfWork(rs.getString("Type_of_work"))
                .natureOfWork(rs.getString("Nature_of_work"))
                .periodOfEmploymentYear(rs.getInt("Period_of_employment_year"))
                .periodOfEmploymentMonth(rs.getInt("Period_of_employment_month"))
                .periodOfEmploymentDay(rs.getInt("Period_of_employment_day"))
                .employmentValidUntil(rs.getDate("Employment_valid_until").toLocalDate())
                .incomePerDay(rs.getBigDecimal("Income_per_day"))
                .benefitPerDay(rs.getBigDecimal("Benefit_per_day"))
                .highestEducation(rs.getString("Highest_education"))
                .workExperience(rs.getBigDecimal("Work_experience"))
                .reasonForNotEmployingThaiPerson(rs.getString("Reason_for_not_employing_thai_person"))
                .createdAt(rs.getTimestamp("Created_at").toLocalDateTime())
                .employeeSnapshot(rs.getString("Employee_snapshot"))
                .employerSnapshot(rs.getString("Employer_snapshot"))
                .employerId(rs.getString("Employer_id"))
                .employeeId(rs.getString("Employee_id"))
                .build();
    }
}
