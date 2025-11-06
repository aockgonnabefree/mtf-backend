package ku.cs.mtf_backend.dto.response;

import ku.cs.mtf_backend.entity.Bill;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BillPrintResponse {
    private Bill bill;                    // ข้อมูล bill (เหมือน getBillById)
    private int printRound;               // รอบที่พิมพ์ (1, 2, 3...)
    private LocalDateTime printedAt;     // เวลาที่พิมพ์
    private String printedByAgentName;   // ชื่อ agent ที่พิมพ์
    private String printReason;           // เหตุผลที่พิมพ์
}