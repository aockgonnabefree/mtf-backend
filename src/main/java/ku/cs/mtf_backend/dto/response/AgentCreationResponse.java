package ku.cs.mtf_backend.dto.response;

import ku.cs.mtf_backend.entity.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentCreationResponse {
    private Agent agent;
    private String plainPassword;
}
