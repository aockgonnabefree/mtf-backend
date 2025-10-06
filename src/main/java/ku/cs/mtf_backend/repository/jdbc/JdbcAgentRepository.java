package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
}
