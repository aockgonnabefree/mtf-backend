package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmploymentContractHistoryDTO {
    private String id;
    private LocalDateTime createdAt;
}
