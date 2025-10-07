package ku.cs.mtf_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WorkStatisticsResponse {
    private long totalWorks;
    private long finishedWorks;
    private long inProgressWorks;
}
