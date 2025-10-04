package ku.cs.mtf_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("DOCUMENT")
public class Document {

    @Id
    private String id;

    private String type;
    private LocalDate expiryDate;
    private String employeeId;
}
