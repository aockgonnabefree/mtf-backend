package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("AGENT")
public class Agent {

    @Id
    private String id;

    private String firstname;
    private String lastname;
    private String email;
    private String hashedPassword;
    private String status;
    private String role;
    private String addressId;
}
