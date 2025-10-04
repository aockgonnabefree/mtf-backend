package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmployerDetailResponse {
    // Employer basic info
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String businessType;
    private String companyName;
    private String status;

    // Financial info
    private Integer financialStatusYear;
    private BigDecimal financialStatusIncome;
    private BigDecimal financialStatusTax;
    private BigDecimal currentIncome;
    private Integer incomeDuration;

    // Address info
    private AddressResponse address;

    @Data
    @Builder
    public static class AddressResponse {
        private String id;
        private String addrDetailTh;
        private String subDistrictTh;
        private String districtTh;
        private String provinceTh;
        private String addrDetailEn;
        private String subDistrictEn;
        private String districtEn;
        private String provinceEn;
        private String postalCode;
    }
}
