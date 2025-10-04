package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmployeeSummaryDTO {
    private String passportNumber;
    private String fullName;
    private String currentEmployer;
    private String status;
    private Map<String, String> documentStatuses;
}
