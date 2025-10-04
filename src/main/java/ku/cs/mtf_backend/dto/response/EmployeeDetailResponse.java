package ku.cs.mtf_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EmployeeDetailResponse {
    // Employee basic info
    private String passportNumber;
    private String firstname;
    private String lastname;
    private String nationality;
    private String bloodType;
    private String status;

    // Address info
    private AddressResponse address;

    // Documents info
    private List<DocumentResponse> documents;

    // Current employer info
    private CurrentEmployerResponse currentEmployer;

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

    @Data
    @Builder
    public static class DocumentResponse {
        private String id;
        private String type;
        private LocalDate expiryDate;
    }

    @Data
    @Builder
    public static class CurrentEmployerResponse {
        private String employerId;
        private String fullName;
        private String companyName;
    }
}
