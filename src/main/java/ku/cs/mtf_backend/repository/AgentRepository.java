package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Agent;

import java.util.Optional;

public interface AgentRepository {
    boolean existsById(String id);
    boolean existsByEmail(String email);
    Agent save(Agent agent);
    Optional<Agent> findById(String id);
    Optional<Agent> findByEmail(String email);
    String generateNextAgentPassword();
}
