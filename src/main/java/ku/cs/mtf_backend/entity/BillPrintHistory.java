package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table("BILL_PRINT_HISTORY")
public class BillPrintHistory {

    @Id
    private String id;

    private String billId;
    private Integer printRound;
    private LocalDateTime printedAt;
    private String printedByAgentId;
    private String printReason;
}