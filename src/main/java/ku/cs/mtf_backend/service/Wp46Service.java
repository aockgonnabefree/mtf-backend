package ku.cs.mtf_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ku.cs.mtf_backend.dto.request.CreateWp46Payload;
import ku.cs.mtf_backend.dto.response.Wp46HistoryDTO;
import ku.cs.mtf_backend.dto.response.Wp46Response;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.entity.Wp46;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.AddressRepository;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import ku.cs.mtf_backend.repository.EmployerRepository;
import ku.cs.mtf_backend.repository.EmploymentRepository;
import ku.cs.mtf_backend.repository.Wp46Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class Wp46Service {

    private Wp46Repository wp46Repository;
    private EmployeeRepository employeeRepository;
    private EmployerRepository employerRepository;
    private AddressRepository addressRepository;
    private EmploymentRepository employmentRepository;
    private ObjectMapper objectMapper;

    public Wp46Service(Wp46Repository wp46Repository,
                       EmployeeRepository employeeRepository,
                       EmployerRepository employerRepository,
                       AddressRepository addressRepository,
                       EmploymentRepository employmentRepository,
                       ObjectMapper objectMapper) {
        this.wp46Repository = wp46Repository;
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.addressRepository = addressRepository;
        this.employmentRepository = employmentRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Wp46 createWp46(CreateWp46Payload payload) {
        // 1. Validate that employment relationship exists
        if (!employmentRepository.existsByEmployerAndEmployee(payload.getEmployerId(), payload.getPassportNo())) {
            throw new IllegalArgumentException(
                    "Employment relationship not found between employer " + payload.getEmployerId() +
                            " and employee " + payload.getPassportNo());
        }

        // 2. Fetch employee with address
        Employee employee = employeeRepository.findById(payload.getPassportNo())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with passport number: " + payload.getPassportNo()));

        Address employeeAddress = addressRepository.findById(employee.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for employee"));

        // 3. Fetch employer with address
        Employer employer = employerRepository.findById(payload.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with ID: " + payload.getEmployerId()));

        Address employerAddress = addressRepository.findById(employer.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for employer"));

        // 4. Build employee snapshot JSON
        Map<String, Object> employeeSnapshot = new LinkedHashMap<>();
        employeeSnapshot.put("passportNumber", employee.getPassportNumber());
        employeeSnapshot.put("firstname", employee.getFirstname());
        employeeSnapshot.put("lastname", employee.getLastname());
        employeeSnapshot.put("nationality", employee.getNationality());
        employeeSnapshot.put("bloodType", employee.getBloodType());
        employeeSnapshot.put("status", employee.getStatus());

        Map<String, Object> employeeAddressMap = new LinkedHashMap<>();
        employeeAddressMap.put("id", employeeAddress.getId());
        employeeAddressMap.put("addrDetailTh", employeeAddress.getAddrDetailTh());
        employeeAddressMap.put("subDistrictTh", employeeAddress.getSubDistrictTh());
        employeeAddressMap.put("districtTh", employeeAddress.getDistrictTh());
        employeeAddressMap.put("provinceTh", employeeAddress.getProvinceTh());
        employeeAddressMap.put("addrDetailEn", employeeAddress.getAddrDetailEn());
        employeeAddressMap.put("subDistrictEn", employeeAddress.getSubDistrictEn());
        employeeAddressMap.put("districtEn", employeeAddress.getDistrictEn());
        employeeAddressMap.put("provinceEn", employeeAddress.getProvinceEn());
        employeeAddressMap.put("postalCode", employeeAddress.getPostalCode());
        employeeSnapshot.put("address", employeeAddressMap);

        // 5. Build employer snapshot JSON
        Map<String, Object> employerSnapshot = new LinkedHashMap<>();
        employerSnapshot.put("id", employer.getId());
        employerSnapshot.put("firstname", employer.getFirstname());
        employerSnapshot.put("lastname", employer.getLastname());
        employerSnapshot.put("email", employer.getEmail());
        employerSnapshot.put("phoneNumber", employer.getPhoneNumber());
        employerSnapshot.put("businessType", employer.getBusinessType());
        employerSnapshot.put("companyName", employer.getCompanyName());
        employerSnapshot.put("status", employer.getStatus());
        employerSnapshot.put("financialStatusYear", employer.getFinancialStatusYear());
        employerSnapshot.put("financialStatusIncome", employer.getFinancialStatusIncome());
        employerSnapshot.put("financialStatusTax", employer.getFinancialStatusTax());
        employerSnapshot.put("currentIncome", employer.getCurrentIncome());
        employerSnapshot.put("incomeDuration", employer.getIncomeDuration());

        Map<String, Object> employerAddressMap = new LinkedHashMap<>();
        employerAddressMap.put("id", employerAddress.getId());
        employerAddressMap.put("addrDetailTh", employerAddress.getAddrDetailTh());
        employerAddressMap.put("subDistrictTh", employerAddress.getSubDistrictTh());
        employerAddressMap.put("districtTh", employerAddress.getDistrictTh());
        employerAddressMap.put("provinceTh", employerAddress.getProvinceTh());
        employerAddressMap.put("addrDetailEn", employerAddress.getAddrDetailEn());
        employerAddressMap.put("subDistrictEn", employerAddress.getSubDistrictEn());
        employerAddressMap.put("districtEn", employerAddress.getDistrictEn());
        employerAddressMap.put("provinceEn", employerAddress.getProvinceEn());
        employerAddressMap.put("postalCode", employerAddress.getPostalCode());
        employerSnapshot.put("address", employerAddressMap);

        // 6. Convert maps to JSON strings
        String employeeSnapshotJson;
        String employerSnapshotJson;
        try {
            employeeSnapshotJson = objectMapper.writeValueAsString(employeeSnapshot);
            employerSnapshotJson = objectMapper.writeValueAsString(employerSnapshot);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize snapshot data to JSON", e);
        }

        // 7. Create and save WP_46
        Wp46 wp46 = Wp46.builder()
                .id(UUID.randomUUID().toString())
                .typeOfWork(payload.getTypeOfWork())
                .natureOfWork(payload.getNatureOfWork())
                .periodOfEmploymentYear(payload.getPeriodOfEmploymentYear())
                .periodOfEmploymentMonth(payload.getPeriodOfEmploymentMonth())
                .periodOfEmploymentDay(payload.getPeriodOfEmploymentDay())
                .employmentValidUntil(LocalDate.parse(payload.getEmploymentValidUntil()))
                .incomePerDay(payload.getIncomePerDay())
                .benefitPerDay(payload.getBenefitPerDay())
                .highestEducation(payload.getHighestEducation())
                .workExperience(payload.getWorkExperience())
                .reasonForNotEmployingThaiPerson(payload.getReasonForNotEmployingThaiPerson())
                .createdAt(LocalDateTime.now())
                .employeeSnapshot(employeeSnapshotJson)
                .employerSnapshot(employerSnapshotJson)
                .employerId(payload.getEmployerId())
                .employeeId(payload.getPassportNo())
                .build();

        return wp46Repository.save(wp46);
    }

    public Wp46Response getWp46ById(String id) {
        Wp46 wp46 = wp46Repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WP-46 document not found with ID: " + id));

        return Wp46Response.builder()
                .id(wp46.getId())
                .typeOfWork(wp46.getTypeOfWork())
                .natureOfWork(wp46.getNatureOfWork())
                .periodOfEmploymentYear(wp46.getPeriodOfEmploymentYear())
                .periodOfEmploymentMonth(wp46.getPeriodOfEmploymentMonth())
                .periodOfEmploymentDay(wp46.getPeriodOfEmploymentDay())
                .employmentValidUntil(wp46.getEmploymentValidUntil())
                .incomePerDay(wp46.getIncomePerDay())
                .benefitPerDay(wp46.getBenefitPerDay())
                .highestEducation(wp46.getHighestEducation())
                .workExperience(wp46.getWorkExperience())
                .reasonForNotEmployingThaiPerson(wp46.getReasonForNotEmployingThaiPerson())
                .createdAt(wp46.getCreatedAt())
                .employeeSnapshot(wp46.getEmployeeSnapshot())
                .employerSnapshot(wp46.getEmployerSnapshot())
                .build();
    }

    public List<Wp46HistoryDTO> getWp46History(String passportNo, String employerId, Integer limit) {
        // employerId is required for history
        if (employerId == null || employerId.isBlank()) {
            throw new IllegalArgumentException("Employer ID is required for fetching WP-46 history");
        }

        int fetchLimit = (limit != null && limit > 0) ? limit : 3;

        List<Wp46> wp46List = wp46Repository.findLatestByEmployeeAndEmployer(passportNo, employerId, fetchLimit);

        return wp46List.stream()
                .map(wp46 -> Wp46HistoryDTO.builder()
                        .id(wp46.getId())
                        .createdAt(wp46.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
