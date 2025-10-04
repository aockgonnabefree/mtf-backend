package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeStatisticsResponse {
    private Long totalEmployees;
    private Long validDocumentEmployees;
    private Long expiringSoonEmployees;
    private Long expiredDocumentEmployees;
}
