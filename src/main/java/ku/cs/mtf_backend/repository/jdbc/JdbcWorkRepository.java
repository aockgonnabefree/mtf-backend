package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Work;
import ku.cs.mtf_backend.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
            INSERT INTO WORK (Id, Current_step_index, Current_step_name, Work_type, Detail, Status, Total_price, Employer_id, under_resp_agent)
            VALUES (:id, :currentStepIndex, :currentStepName, CAST(:workType AS work_type), :detail, CAST(:status AS work_status), :totalPrice, :employerId, :underRespAgent)
            """;

        jdbcClient.sql(sql)
                .param("id", work.getId())
                .param("currentStepIndex", work.getCurrentStepIndex())
                .param("currentStepName", work.getCurrentStepName())
                .param("workType", work.getWorkType())
                .param("detail", work.getDetail())
                .param("status", work.getStatus())
                .param("totalPrice", work.getTotalPrice())
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
        String sql = "UPDATE WORK SET Current_step_index = :stepIndex, Current_step_name = :stepName WHERE Id = :workId";
        return jdbcClient.sql(sql)
                .param("stepIndex", stepIndex)
                .param("stepName", stepName)
                .param("workId", workId)
                .update();
    }

    @Override
    public int updateStatus(String workId, String status) {
        String sql = "UPDATE WORK SET Status = CAST(:status AS work_status) WHERE Id = :workId";
        return jdbcClient.sql(sql)
                .param("status", status)
                .param("workId", workId)
                .update();
    }
}
