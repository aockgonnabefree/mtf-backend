package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployerSummaryDTO {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Long activeEmployeeCount;
    private String status;
}
