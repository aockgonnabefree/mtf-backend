package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentDetailResponse {
    // Agent basic info
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String status;

    // Address info (Thai only)
    private AddressResponse address;

    @Data
    @Builder
    public static class AddressResponse {
        private String id;
        private String addrDetailTh;
        private String subDistrictTh;
        private String districtTh;
        private String provinceTh;
        private String postalCode;
    }
}