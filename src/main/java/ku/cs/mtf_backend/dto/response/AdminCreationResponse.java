package ku.cs.mtf_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminCreationResponse {

    private String message;
    private String adminId;
    private String fullname;
    private String email;
    private String role;
    private String status;
}