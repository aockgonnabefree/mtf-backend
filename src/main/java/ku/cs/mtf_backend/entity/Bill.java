package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Table("BILL")
public class Bill {

    @Id
    private String id;

    private Integer stepIndex;
    private String stepName;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String workId;

    // Print tracking fields
    private Integer printCount;
    private LocalDateTime lastPrintedAt;
    private String printStatus;
}
