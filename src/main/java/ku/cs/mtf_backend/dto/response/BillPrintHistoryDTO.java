package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BillPrintHistoryDTO {
    private int printRound;               // รอบที่พิมพ์
    private LocalDateTime printedAt;     // เวลาที่พิมพ์
    private String printedByAgentName;   // ชื่อ agent ที่พิมพ์
    private String printReason;           // เหตุผลที่พิมพ์
}