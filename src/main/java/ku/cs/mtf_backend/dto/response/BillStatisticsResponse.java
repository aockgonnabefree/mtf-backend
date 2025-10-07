package ku.cs.mtf_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BillStatisticsResponse {
    private long totalBills;
    private long paidBills;
    private long unpaidBills;
}
