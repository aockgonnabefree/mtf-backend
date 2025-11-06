package ku.cs.mtf_backend.dto.request;

import lombok.Data;

@Data
public class PrintBillRequest {
    private String agentId;  // รหัส agent ที่ทำการพิมพ์ (required)
    private String reason;   // เหตุผลในการพิมพ์ (optional)
}