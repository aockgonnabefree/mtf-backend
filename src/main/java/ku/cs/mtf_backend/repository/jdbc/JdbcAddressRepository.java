package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcAddressRepository implements AddressRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcAddressRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Address> findById(String id) {
        String sql = "SELECT * FROM ADDRESS WHERE Id = :id";
        // .query(Address.class) จะใช้ RowMapper เพื่อ map column ไปยัง field ใน Address class โดยอัตโนมัติ
        // เช่น คอลัมน์ "Addr_detail_th" จะถูก map ไปที่ field "addrDetailTh"
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(Address.class)
                .optional();
    }

    @Override
    public Optional<Address> findByDetails(String addrDetailTh, String subDistrictTh, String districtTh, String provinceTh, String postalCode) {
        String sql = """
            SELECT * FROM ADDRESS
            WHERE Addr_detail_th = :addrDetailTh
              AND Sub_district_th = :subDistrictTh
              AND District_th = :districtTh
              AND Province_th = :provinceTh
              AND Postal_code = :postalCode
            LIMIT 1
            """;
        return jdbcClient.sql(sql)
                .param("addrDetailTh", addrDetailTh)
                .param("subDistrictTh", subDistrictTh)
                .param("districtTh", districtTh)
                .param("provinceTh", provinceTh)
                .param("postalCode", postalCode)
                .query(Address.class)
                .optional();
    }

    @Override
    public Address save(Address address) {
        String sql = """
                INSERT INTO ADDRESS (Id, Addr_detail_th, Sub_district_th, District_th, Province_th,
                                     Addr_detail_en, Sub_district_en, District_en, Province_en, Postal_code)
                VALUES (:id, :addrDetailTh, :subDistrictTh, :districtTh, :provinceTh,
                        :addrDetailEn, :subDistrictEn, :districtEn, :provinceEn, :postalCode)
                """;

        jdbcClient.sql(sql)
                .param("id", address.getId())
                .param("addrDetailTh", address.getAddrDetailTh())
                .param("subDistrictTh", address.getSubDistrictTh())
                .param("districtTh", address.getDistrictTh())
                .param("provinceTh", address.getProvinceTh())
                .param("addrDetailEn", address.getAddrDetailEn())
                .param("subDistrictEn", address.getSubDistrictEn())
                .param("districtEn", address.getDistrictEn())
                .param("provinceEn", address.getProvinceEn())
                .param("postalCode", address.getPostalCode())
                .update();

        return address;
    }

    @Override
    public Address update(Address address) {
        String sql = """
            UPDATE ADDRESS SET
                Addr_detail_th = :addrDetailTh,
                Sub_district_th = :subDistrictTh,
                District_th = :districtTh,
                Province_th = :provinceTh,
                Addr_detail_en = :addrDetailEn,
                Sub_district_en = :subDistrictEn,
                District_en = :districtEn,
                Province_en = :provinceEn,
                Postal_code = :postalCode
            WHERE Id = :id
            """;
        jdbcClient.sql(sql)
                .param("id", address.getId())
                .param("addrDetailTh", address.getAddrDetailTh())
                .param("subDistrictTh", address.getSubDistrictTh())
                .param("districtTh", address.getDistrictTh())
                .param("provinceTh", address.getProvinceTh())
                .param("addrDetailEn", address.getAddrDetailEn())
                .param("subDistrictEn", address.getSubDistrictEn())
                .param("districtEn", address.getDistrictEn())
                .param("provinceEn", address.getProvinceEn())
                .param("postalCode", address.getPostalCode())
                .update();
        return address;
    }
}
