package ku.cs.mtf_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String agentId;
    private String fullname;
    private String email;
    private String role;
    private String status;
}