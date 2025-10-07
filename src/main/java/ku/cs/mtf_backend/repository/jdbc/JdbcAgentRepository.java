package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.dto.projection.AgentSummary;
import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAgentRepository implements AgentRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcAgentRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM AGENT WHERE Id = :id";
        Integer count = jdbcClient.sql(sql).param("id", id).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM AGENT WHERE Email = :email";
        Integer count = jdbcClient.sql(sql).param("email", email).query(Integer.class).single();
        return count > 0;
    }

    @Override
    public Agent save(Agent agent) {
        String sql = """
            INSERT INTO AGENT (Id, Firstname, Lastname, Email, Hashed_password, Status, Address_id)
            VALUES (:id, :firstname, :lastname, :email, :hashedPassword, CAST(:status AS active_status_type), :addressId)
            """;

        jdbcClient.sql(sql)
                .param("id", agent.getId())
                .param("firstname", agent.getFirstname())
                .param("lastname", agent.getLastname())
                .param("email", agent.getEmail())
                .param("hashedPassword", agent.getHashedPassword())
                .param("status", agent.getStatus())
                .param("addressId", agent.getAddressId())
                .update();

        return agent;
    }

    @Override
    public Optional<Agent> findById(String id) {
        String sql = "SELECT * FROM AGENT WHERE Id = :id";
        return jdbcClient.sql(sql).param("id", id).query(Agent.class).optional();
    }

    @Override
    public Optional<Agent> findByEmail(String email) {
        String sql = "SELECT * FROM AGENT WHERE Email = :email";
        return jdbcClient.sql(sql).param("email", email).query(Agent.class).optional();
    }

    @Override
    public String generateNextAgentPassword() {
        String sql = """
            SELECT 'mtf' || LPAD((COUNT(*) + 1)::TEXT, 3, '0')
            FROM AGENT
            """;

        return jdbcClient.sql(sql).query(String.class).single();
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM AGENT";
        return jdbcClient.sql(sql).query(Long.class).single();
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM AGENT WHERE Status = CAST(:status AS active_status_type)";
        return jdbcClient.sql(sql)
                .param("status", status)
                .query(Long.class)
                .single();
    }

    @Override
    public List<AgentSummary> findAllSummariesWithFilters(String fullName, String status,
                                                           int offset, int limit) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                Id as id,
                CONCAT(Firstname, ' ', Lastname) as fullName,
                Email as email,
                Status as status
            FROM AGENT
            WHERE 1=1
            """);

        if (fullName != null && !fullName.isBlank()) {
            sql.append(" AND (Firstname ILIKE :fullName OR Lastname ILIKE :fullName)");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND Status = CAST(:status AS active_status_type)");
        }

        sql.append(" ORDER BY CASE WHEN Status = 'ACTIVE' THEN 0 ELSE 1 END, Firstname ASC");
        sql.append(" OFFSET :offset LIMIT :limit");

        var query = jdbcClient.sql(sql.toString())
                .param("offset", offset)
                .param("limit", limit);

        if (fullName != null && !fullName.isBlank()) {
            query = query.param("fullName", "%" + fullName + "%");
        }
        if (status != null && !status.isBlank()) {
            query = query.param("status", status);
        }

        return query.query(this::mapRowToAgentSummary).list();
    }

    @Override
    public long countWithFilters(String fullName, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM AGENT
            WHERE 1=1
            """);

        if (fullName != null && !fullName.isBlank()) {
            sql.append(" AND (Firstname ILIKE :fullName OR Lastname ILIKE :fullName)");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND Status = CAST(:status AS active_status_type)");
        }

        var query = jdbcClient.sql(sql.toString());

        if (fullName != null && !fullName.isBlank()) {
            query = query.param("fullName", "%" + fullName + "%");
        }
        if (status != null && !status.isBlank()) {
            query = query.param("status", status);
        }

        return query.query(Long.class).single();
    }

    private AgentSummary mapRowToAgentSummary(ResultSet rs, int rowNum) throws SQLException {
        return new AgentSummary(
                rs.getString("id"),
                rs.getString("fullName"),
                rs.getString("email"),
                rs.getString("status")
        );
    }
}
