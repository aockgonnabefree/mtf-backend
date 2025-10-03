package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Address;

import java.util.Optional;

public interface AddressRepository {

    Optional<Address> findById(String id);
    Optional<Address> findByDetails(String addrDetailTh, String subDistrictTh, String districtTh, String provinceTh, String postalCode);
    Address save(Address address);
    Address update(Address address);
}
