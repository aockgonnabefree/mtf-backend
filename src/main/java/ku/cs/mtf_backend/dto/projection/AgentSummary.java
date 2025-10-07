package ku.cs.mtf_backend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSummary {
    private String id;
    private String fullName;
    private String email;
    private String status;
}
