package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.dto.projection.AgentSummary;
import ku.cs.mtf_backend.entity.Agent;

import java.util.List;
import java.util.Optional;

public interface AgentRepository {
    boolean existsById(String id);
    boolean existsByEmail(String email);
    Agent save(Agent agent);
    Agent update(Agent agent);
    Optional<Agent> findById(String id);
    Optional<Agent> findByEmail(String email);
    String generateNextAgentPassword();

    // Statistics
    long countAll();
    long countByStatus(String status);

    // List with filters and pagination
    List<AgentSummary> findAllSummariesWithFilters(String fullName, String status,
                                                    int offset, int limit);
    long countWithFilters(String fullName, String status);
}
