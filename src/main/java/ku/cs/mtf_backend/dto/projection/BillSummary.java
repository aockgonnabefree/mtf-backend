package ku.cs.mtf_backend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillSummary {
    private String billId;
    private String employerName;
    private String workType;
    private Integer stepIndex;
    private String stepName;
    private BigDecimal price;
    private String paymentStatus;
}
