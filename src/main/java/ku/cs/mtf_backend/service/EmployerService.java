package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateEmployerPayload;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EmployerService {
    private final EmployerRepository employerRepository;
    private final AddressService addressService;

    @Autowired
    public EmployerService(EmployerRepository employerRepository, AddressService addressService) {
        this.employerRepository = employerRepository;
        this.addressService = addressService;
    }

    @Transactional
    public Employer createEmployer(CreateEmployerPayload payload) {
        // 1. Validate for duplicate employer.
        if (employerRepository.existsById(payload.getId())) {
            throw new IllegalArgumentException("Employer with ID " + payload.getId() + " already exists.");
        }
        if (employerRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email " + payload.getEmail() + " is already in use.");
        }
        if (employerRepository.existsByPhoneNumber(payload.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number " + payload.getPhoneNumber() + " is already in use.");
        }

        // 2. Delegate address handling to AddressService.
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 3. Build the Employer entity with data from the payload and the managed Address.
        Employer employerToSave = Employer.builder()
                .id(payload.getId())
                .firstname(payload.getFirstName())
                .lastname(payload.getLastName())
                .email(payload.getEmail())
                .phoneNumber(payload.getPhoneNumber())
                .businessType(payload.getBusinessType())
                .financialStatusYear(payload.getFinancialStatusYear())
                .financialStatusIncome(BigDecimal.valueOf(payload.getFinancialStatusIncome()))
                .financialStatusTax(BigDecimal.valueOf(payload.getFinancialStatusTax()))
                .currentIncome(BigDecimal.valueOf(payload.getCurrentIncome()))
                .incomeDuration(payload.getIncomeDuration())
                .status(payload.getStatus())
                .companyName(payload.getCompanyName())
                .addressId(address.getId())
                .build();

        // 4. Save the new Employer and return it.
        return employerRepository.save(employerToSave);
    }


}
