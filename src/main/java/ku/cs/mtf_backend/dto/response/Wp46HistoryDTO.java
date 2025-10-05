package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Wp46HistoryDTO {
    private String id;
    private LocalDateTime createdAt;
}
