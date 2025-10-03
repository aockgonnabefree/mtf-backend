package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateAddressPayload;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService {
    private AddressRepository repository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.repository = addressRepository;
    }

    public Address findOrCreateAddress(CreateAddressPayload payload) {
        // 1. Try to find an existing address with the exact same details.
        Optional<Address> existingAddress = repository.findByDetails(
                payload.getAddrDetailTh(),
                payload.getSubDistrictTh(),
                payload.getDistrictTh(),
                payload.getProvinceTh(),
                payload.getPostalCode()
        );

        // 2. If found, return the existing address to be reused.
        if (existingAddress.isPresent()) {
            return existingAddress.get();
        }

        // 3. If not found, create a new Address entity, save it, and return.
        Address newAddress = Address.builder()
                .id(UUID.randomUUID().toString())
                .addrDetailTh(payload.getAddrDetailTh())
                .subDistrictTh(payload.getSubDistrictTh())
                .districtTh(payload.getDistrictTh())
                .provinceTh(payload.getProvinceTh())
                .addrDetailEn(payload.getAddrDetailEn())
                .subDistrictEn(payload.getSubDistrictEn())
                .districtEn(payload.getDistrictEn())
                .provinceEn(payload.getProvinceEn())
                .postalCode(payload.getPostalCode())
                .build();
        return repository.save(newAddress);
    }
}
