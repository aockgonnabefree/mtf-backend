package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.projection.EmployerSummary;
import ku.cs.mtf_backend.dto.request.CreateEmployerPayload;
import ku.cs.mtf_backend.dto.request.UpdateEmployerPayload;
import ku.cs.mtf_backend.dto.response.EmployerDetailResponse;
import ku.cs.mtf_backend.dto.response.EmployerStatisticsResponse;
import ku.cs.mtf_backend.dto.response.EmployerSummaryDTO;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.AddressRepository;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployerService {
    private final EmployerRepository employerRepository;
    private final AddressService addressService;
    private final AddressRepository addressRepository;

    @Autowired
    public EmployerService(EmployerRepository employerRepository, AddressService addressService,
                           AddressRepository addressRepository) {
        this.employerRepository = employerRepository;
        this.addressService = addressService;
        this.addressRepository = addressRepository;
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

    @Transactional
    public Employer updateEmployer(String employerId, UpdateEmployerPayload payload) {
        // 1. ค้นหานายจ้างคนเดิม ถ้าไม่เจอ โยน Exception 404 Not Found
        Employer existingEmployer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));

        // 2. ตรวจสอบ Email ซ้ำ (ต้องไม่ใช่ของตัวเอง)
        Optional<Employer> employerWithSameEmail = employerRepository.findByEmail(payload.getEmail());
        if (employerWithSameEmail.isPresent() && !employerWithSameEmail.get().getId().equals(employerId)) {
            throw new IllegalArgumentException("Email " + payload.getEmail() + " is already in use by another employer.");
        }

        // 3. ตรวจสอบเบอร์โทรซ้ำ (ต้องไม่ใช่ของตัวเอง)
        Optional<Employer> employerWithSamePhone = employerRepository.findByPhoneNumber(payload.getPhoneNumber());
        if (employerWithSamePhone.isPresent() && !employerWithSamePhone.get().getId().equals(employerId)) {
            throw new IllegalArgumentException("Phone number " + payload.getPhoneNumber() + " is already in use by another employer.");
        }

        // 4. จัดการเรื่องที่อยู่ (หาหรือสร้างใหม่)
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 5. อัปเดตข้อมูลใน object เดิม
        existingEmployer.setFirstname(payload.getFirstName());
        existingEmployer.setLastname(payload.getLastName());
        existingEmployer.setEmail(payload.getEmail());
        existingEmployer.setPhoneNumber(payload.getPhoneNumber());
        existingEmployer.setBusinessType(payload.getBusinessType());
        existingEmployer.setFinancialStatusYear(payload.getFinancialStatusYear());
        existingEmployer.setFinancialStatusIncome(BigDecimal.valueOf(payload.getFinancialStatusIncome()));
        existingEmployer.setFinancialStatusTax(BigDecimal.valueOf(payload.getFinancialStatusTax()));
        existingEmployer.setCurrentIncome(BigDecimal.valueOf(payload.getCurrentIncome()));
        existingEmployer.setIncomeDuration(payload.getIncomeDuration());
        existingEmployer.setStatus(payload.getStatus());
        existingEmployer.setCompanyName(payload.getCompanyName());
        existingEmployer.setAddressId(address.getId());

        // 6. บันทึกการเปลี่ยนแปลง
        return employerRepository.update(existingEmployer);
    }

    public EmployerStatisticsResponse getStatistics() {
        long totalEmployers = employerRepository.countAll();
        long activeEmployers = employerRepository.countByStatus("ACTIVE");
        long inactiveEmployers = employerRepository.countByStatus("INACTIVE");

        return EmployerStatisticsResponse.builder()
                .totalEmployers(totalEmployers)
                .activeEmployers(activeEmployers)
                .inactiveEmployers(inactiveEmployers)
                .build();
    }

    public PageResponse<EmployerSummaryDTO> getEmployersWithPagination(Integer page, Integer size, String nameContains, String status) {
        // 1. Validate parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (status != null && !status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException("Status must be either 'ACTIVE' or 'INACTIVE'.");
        }

        // 2. Fetch data from repository
        List<EmployerSummary> employers = employerRepository.findAllWithPagination(page, size, nameContains, status);

        // 3. Convert to DTO
        List<EmployerSummaryDTO> employerDTOs = employers.stream()
                .map(e -> EmployerSummaryDTO.builder()
                        .id(e.getId())
                        .fullName(e.getFullName())
                        .phoneNumber(e.getPhoneNumber())
                        .email(e.getEmail())
                        .activeEmployeeCount(e.getActiveEmployeeCount())
                        .status(e.getStatus())
                        .build())
                .collect(Collectors.toList());

        // 4. Calculate total elements (for simplicity, we count all matching records)
        long totalElements = employerRepository.countAll();

        // 5. Calculate total pages
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<EmployerSummaryDTO>builder()
                .content(employerDTOs)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    public EmployerDetailResponse getEmployerById(String employerId) {
        // 1. ค้นหา Employer
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));

        // 2. ค้นหา Address
        Address address = addressRepository.findById(employer.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + employer.getAddressId()));

        // 3. Build response
        return EmployerDetailResponse.builder()
                .id(employer.getId())
                .firstname(employer.getFirstname())
                .lastname(employer.getLastname())
                .email(employer.getEmail())
                .phoneNumber(employer.getPhoneNumber())
                .businessType(employer.getBusinessType())
                .companyName(employer.getCompanyName())
                .status(employer.getStatus())
                .financialStatusYear(employer.getFinancialStatusYear())
                .financialStatusIncome(employer.getFinancialStatusIncome())
                .financialStatusTax(employer.getFinancialStatusTax())
                .currentIncome(employer.getCurrentIncome())
                .incomeDuration(employer.getIncomeDuration())
                .address(EmployerDetailResponse.AddressResponse.builder()
                        .id(address.getId())
                        .addrDetailTh(address.getAddrDetailTh())
                        .subDistrictTh(address.getSubDistrictTh())
                        .districtTh(address.getDistrictTh())
                        .provinceTh(address.getProvinceTh())
                        .addrDetailEn(address.getAddrDetailEn())
                        .subDistrictEn(address.getSubDistrictEn())
                        .districtEn(address.getDistrictEn())
                        .provinceEn(address.getProvinceEn())
                        .postalCode(address.getPostalCode())
                        .build())
                .build();
    }

}
