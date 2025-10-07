package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Table("PRICING")
public class Pricing {

    @Id
    private String id;

    private String workType;
    private BigDecimal pricePerEmployee;
    private LocalDateTime updatedAt;
}
