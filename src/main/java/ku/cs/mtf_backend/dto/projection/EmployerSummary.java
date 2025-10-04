package ku.cs.mtf_backend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerSummary {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Long activeEmployeeCount;
    private String status;
}
