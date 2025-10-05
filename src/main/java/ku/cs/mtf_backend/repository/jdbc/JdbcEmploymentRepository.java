package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Employment;
import ku.cs.mtf_backend.repository.EmploymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcEmploymentRepository implements EmploymentRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcEmploymentRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(Employment employment) {
        String sql = """
            INSERT INTO EMPLOYMENT (Employer_id, Employee_id, Status)
            VALUES (:employerId, :employeeId, CAST(:status AS active_status_type))
            """;

        jdbcClient.sql(sql)
                .param("employerId", employment.getEmployerId())
                .param("employeeId", employment.getEmployeeId())
                .param("status", employment.getStatus())
                .update();
    }

    @Override
    public Optional<Employment> findActiveByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM EMPLOYMENT WHERE Employee_id = :employeeId AND Status = 'ACTIVE'";
        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .query(Employment.class)
                .optional();
    }

    @Override
    public Optional<Employment> findByEmployerIdAndEmployeeId(String employerId, String employeeId) {
        String sql = "SELECT * FROM EMPLOYMENT WHERE Employer_id = :employerId AND Employee_id = :employeeId";
        return jdbcClient.sql(sql)
                .param("employerId", employerId)
                .param("employeeId", employeeId)
                .query(Employment.class)
                .optional();
    }

    @Override
    public int updateStatus(String employerId, String employeeId, String status) {
        String sql = "UPDATE EMPLOYMENT SET Status = CAST(:status AS active_status_type) WHERE Employer_id = :employerId AND Employee_id = :employeeId";
        return jdbcClient.sql(sql)
                .param("status", status)
                .param("employerId", employerId)
                .param("employeeId", employeeId)
                .update();
    }

    @Override
    public Optional<Employment> findByEmployeeIdAndStatus(String employeeId, String status) {
        String sql = "SELECT * FROM EMPLOYMENT WHERE Employee_id = :employeeId AND Status = CAST(:status AS active_status_type)";
        return jdbcClient.sql(sql)
                .param("employeeId", employeeId)
                .param("status", status)
                .query(Employment.class)
                .optional();
    }

    @Override
    public boolean existsByEmployerAndEmployee(String employerId, String employeeId) {
        String sql = "SELECT COUNT(*) FROM EMPLOYMENT WHERE Employer_id = :employerId AND Employee_id = :employeeId";
        Integer count = jdbcClient.sql(sql)
                .param("employerId", employerId)
                .param("employeeId", employeeId)
                .query(Integer.class)
                .single();
        return count > 0;
    }
}
