package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@Table("WORK")
public class Work {

    @Id
    private String id;

    private Integer currentStepIndex;
    private String currentStepName;
    private String workType;
    private String detail;
    private String status;
    private BigDecimal totalPrice;
    private String employerId;
    private String underRespAgent;
}
