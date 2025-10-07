package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.dto.projection.WorkSummary;
import ku.cs.mtf_backend.entity.Work;
import ku.cs.mtf_backend.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcWorkRepository implements WorkRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcWorkRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Work save(Work work) {
        String sql = """
            INSERT INTO WORK (Id, Current_step_index, Current_step_name, Work_type, Detail, Status, Total_price, Updated_at, Employer_id, under_resp_agent)
            VALUES (:id, :currentStepIndex, :currentStepName, CAST(:workType AS work_type), :detail, CAST(:status AS work_status), :totalPrice, :updatedAt, :employerId, :underRespAgent)
            """;

        jdbcClient.sql(sql)
                .param("id", work.getId())
                .param("currentStepIndex", work.getCurrentStepIndex())
                .param("currentStepName", work.getCurrentStepName())
                .param("workType", work.getWorkType())
                .param("detail", work.getDetail())
                .param("status", work.getStatus())
                .param("totalPrice", work.getTotalPrice())
                .param("updatedAt", work.getUpdatedAt())
                .param("employerId", work.getEmployerId())
                .param("underRespAgent", work.getUnderRespAgent())
                .update();

        return work;
    }

    @Override
    public Optional<Work> findById(String id) {
        String sql = "SELECT * FROM WORK WHERE Id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(Work.class)
                .optional();
    }

    @Override
    public int updateStep(String workId, int stepIndex, String stepName) {
        String sql = "UPDATE WORK SET Current_step_index = :stepIndex, Current_step_name = :stepName, Updated_at = CURRENT_TIMESTAMP WHERE Id = :workId";
        return jdbcClient.sql(sql)
                .param("stepIndex", stepIndex)
                .param("stepName", stepName)
                .param("workId", workId)
                .update();
    }

    @Override
    public int updateStatus(String workId, String status) {
        String sql = "UPDATE WORK SET Status = CAST(:status AS work_status), Updated_at = CURRENT_TIMESTAMP WHERE Id = :workId";
        return jdbcClient.sql(sql)
                .param("status", status)
                .param("workId", workId)
                .update();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM WORK";
        return jdbcClient.sql(sql).query(Long.class).single();
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM WORK WHERE Status = CAST(:status AS work_status)";
        return jdbcClient.sql(sql)
                .param("status", status)
                .query(Long.class)
                .single();
    }

    @Override
    public List<WorkSummary> findAllSummariesWithFilters(String employerName, String workType, String status,
                                                          int offset, int limit) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                w.Id as id,
                w.Work_type as workType,
                CONCAT(e.Firstname, ' ', e.Lastname) as employerName,
                e.Company_name as companyName,
                w.Current_step_name as currentStep,
                w.Status as status,
                w.Updated_at as updatedAt
            FROM WORK w
            JOIN EMPLOYER e ON w.Employer_id = e.Id
            WHERE 1=1
            """);

        if (employerName != null && !employerName.isBlank()) {
            sql.append(" AND (e.Firstname ILIKE :employerName OR e.Lastname ILIKE :employerName OR e.Company_name ILIKE :employerName)");
        }
        if (workType != null && !workType.isBlank()) {
            sql.append(" AND w.Work_type = CAST(:workType AS work_type)");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND w.Status = CAST(:status AS work_status)");
        }

        sql.append(" ORDER BY CASE WHEN w.Status = 'NOT_FINISHED' THEN 0 ELSE 1 END, w.Updated_at DESC");
        sql.append(" OFFSET :offset LIMIT :limit");

        var query = jdbcClient.sql(sql.toString())
                .param("offset", offset)
                .param("limit", limit);

        if (employerName != null && !employerName.isBlank()) {
            query = query.param("employerName", "%" + employerName + "%");
        }
        if (workType != null && !workType.isBlank()) {
            query = query.param("workType", workType);
        }
        if (status != null && !status.isBlank()) {
            query = query.param("status", status);
        }

        return query.query(this::mapRowToWorkSummary).list();
    }

    private WorkSummary mapRowToWorkSummary(ResultSet rs, int rowNum) throws SQLException {
        return new WorkSummary(
                rs.getString("id"),
                rs.getString("workType"),
                rs.getString("employerName"),
                rs.getString("companyName"),
                rs.getString("currentStep"),
                rs.getString("status"),
                rs.getTimestamp("updatedAt") != null ? rs.getTimestamp("updatedAt").toLocalDateTime() : null
        );
    }

    @Override
    public long countWithFilters(String employerName, String workType, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM WORK w
            JOIN EMPLOYER e ON w.Employer_id = e.Id
            WHERE 1=1
            """);

        if (employerName != null && !employerName.isBlank()) {
            sql.append(" AND (e.Firstname ILIKE :employerName OR e.Lastname ILIKE :employerName OR e.Company_name ILIKE :employerName)");
        }
        if (workType != null && !workType.isBlank()) {
            sql.append(" AND w.Work_type = CAST(:workType AS work_type)");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND w.Status = CAST(:status AS work_status)");
        }

        var query = jdbcClient.sql(sql.toString());

        if (employerName != null && !employerName.isBlank()) {
            query = query.param("employerName", "%" + employerName + "%");
        }
        if (workType != null && !workType.isBlank()) {
            query = query.param("workType", workType);
        }
        if (status != null && !status.isBlank()) {
            query = query.param("status", status);
        }

        return query.query(Long.class).single();
    }
}
