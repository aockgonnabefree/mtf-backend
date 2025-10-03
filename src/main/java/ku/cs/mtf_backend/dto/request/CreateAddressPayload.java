package ku.cs.mtf_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CreateAddressPayload {
    @NotBlank(message = "Address detail (TH) is required")
    private String addrDetailTh;

    @NotBlank(message = "Sub-district (TH) is required")
    private String subDistrictTh;

    @NotBlank(message = "District (TH) is required")
    private String districtTh;

    @NotBlank(message = "Province (TH) is required")
    private String provinceTh;

    private String addrDetailEn;
    private String subDistrictEn;
    private String districtEn;
    private String provinceEn;

    @NotBlank(message = "Postal code is required")
    @Size(min = 5, max = 5, message = "Postal code must be 5 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Postal code must contain only digits")
    private String postalCode;

    public void setAddrDetailTh(String addrDetailTh) {
        this.addrDetailTh = (addrDetailTh == null) ? null : addrDetailTh.strip();
    }

    public void setSubDistrictTh(String subDistrictTh) {
        this.subDistrictTh = (subDistrictTh == null) ? null : subDistrictTh.strip();
    }

    public void setDistrictTh(String districtTh) {
        this.districtTh = (districtTh == null) ? null : districtTh.strip();
    }

    public void setProvinceTh(String provinceTh) {
        this.provinceTh = (provinceTh == null) ? null : provinceTh.strip();
    }

    public void setAddrDetailEn(String addrDetailEn) {
        this.addrDetailEn = (addrDetailEn == null) ? null : addrDetailEn.strip();
    }

    public void setSubDistrictEn(String subDistrictEn) {
        this.subDistrictEn = (subDistrictEn == null) ? null : subDistrictEn.strip();
    }

    public void setDistrictEn(String districtEn) {
        this.districtEn = (districtEn == null) ? null : districtEn.strip();
    }

    public void setProvinceEn(String provinceEn) {
        this.provinceEn = (provinceEn == null) ? null : provinceEn.strip();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = (postalCode == null) ? null : postalCode.strip();
    }
}
