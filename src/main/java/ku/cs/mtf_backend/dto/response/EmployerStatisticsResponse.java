package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployerStatisticsResponse {
    private Long totalEmployers;
    private Long activeEmployers;
    private Long inactiveEmployers;
}
