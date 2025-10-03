package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("EMPLOYMENT")
public class Employment {

    private String employerId;
    private String employeeId;
    private String status;
}
