package ku.cs.mtf_backend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSummary {
    private String id;
    private String workType;
    private String employerName;
    private String companyName;
    private String currentStep;
    private String status;
    private LocalDateTime updatedAt;
}
