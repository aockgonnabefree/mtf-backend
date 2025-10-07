package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeAvailabilityDTO {
    private String passportNumber;
    private String fullName;
    private String nationality;
    private String status;
    private Boolean isInThisWork;
}
