package ku.cs.mtf_backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("ADDRESS")
public class Address {
    @Id
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
