package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.WorkDetail;
import ku.cs.mtf_backend.repository.WorkDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcWorkDetailRepository implements WorkDetailRepository {

    private JdbcClient jdbcClient;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcWorkDetailRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(WorkDetail workDetail) {
        String sql = """
            INSERT INTO WORK_DETAIL (Work_id, Employee_id)
            VALUES (:workId, :employeeId)
            """;

        jdbcClient.sql(sql)
                .param("workId", workDetail.getWorkId())
                .param("employeeId", workDetail.getEmployeeId())
                .update();
    }

    @Override
    public void batchInsert(List<WorkDetail> workDetails) {
        if (workDetails == null || workDetails.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO WORK_DETAIL (Work_id, Employee_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                WorkDetail wd = workDetails.get(i);
                ps.setString(1, wd.getWorkId());
                ps.setString(2, wd.getEmployeeId());
            }

            @Override
            public int getBatchSize() {
                return workDetails.size();
            }
        });
    }

    @Override
    public List<WorkDetail> findAllByWorkId(String workId) {
        String sql = "SELECT * FROM WORK_DETAIL WHERE Work_id = :workId";
        return jdbcClient.sql(sql)
                .param("workId", workId)
                .query(WorkDetail.class)
                .list();
    }

    @Override
    public List<String> findEmployeeIdsByWorkId(String workId) {
        String sql = "SELECT Employee_id FROM WORK_DETAIL WHERE Work_id = :workId";
        return jdbcClient.sql(sql)
                .param("workId", workId)
                .query(String.class)
                .list();
    }
}
