package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table("DOCUMENT")
public class Document {

    @Id
    private String id;

    private String type;
    private LocalDate expiryDate;
    private String employeeId;
}
