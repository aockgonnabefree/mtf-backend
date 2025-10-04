package ku.cs.mtf_backend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummary {
    private String passportNumber;
    private String fullName;
    private String currentEmployer;
    private String status;
}
