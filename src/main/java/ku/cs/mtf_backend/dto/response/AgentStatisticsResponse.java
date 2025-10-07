package ku.cs.mtf_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AgentStatisticsResponse {
    private long totalAgents;
    private long activeAgents;
    private long inactiveAgents;
}
