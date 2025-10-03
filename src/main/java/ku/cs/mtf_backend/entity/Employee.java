package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("EMPLOYEE")
public class Employee {

    @Id
    private String passportNumber;

    private String firstname;
    private String lastname;
    private String nationality;
    private String bloodType;
    private String status;
    private String addressId;
}
