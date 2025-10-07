package ku.cs.mtf_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("WORK_DETAIL")
public class WorkDetail {

    private String workId;
    private String employeeId;
}
