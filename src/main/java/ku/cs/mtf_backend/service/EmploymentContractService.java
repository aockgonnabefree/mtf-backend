package ku.cs.mtf_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ku.cs.mtf_backend.dto.request.CreateEmploymentContractPayload;
import ku.cs.mtf_backend.dto.response.EmploymentContractHistoryDTO;
import ku.cs.mtf_backend.dto.response.EmploymentContractResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.entity.EmploymentContract;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.AddressRepository;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import ku.cs.mtf_backend.repository.EmployerRepository;
import ku.cs.mtf_backend.repository.EmploymentContractRepository;
import ku.cs.mtf_backend.repository.EmploymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmploymentContractService {

    private EmploymentContractRepository employmentContractRepository;
    private EmployeeRepository employeeRepository;
    private EmployerRepository employerRepository;
    private AddressRepository addressRepository;
    private EmploymentRepository employmentRepository;
    private ObjectMapper objectMapper;

    public EmploymentContractService(EmploymentContractRepository employmentContractRepository,
                                     EmployeeRepository employeeRepository,
                                     EmployerRepository employerRepository,
                                     AddressRepository addressRepository,
                                     EmploymentRepository employmentRepository,
                                     ObjectMapper objectMapper) {
        this.employmentContractRepository = employmentContractRepository;
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.addressRepository = addressRepository;
        this.employmentRepository = employmentRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EmploymentContract createEmploymentContract(CreateEmploymentContractPayload payload) {
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

        // 7. Create and save Employment Contract
        EmploymentContract contract = EmploymentContract.builder()
                .id(UUID.randomUUID().toString())
                .typeOfWorkTh(payload.getTypeOfWorkTh())
                .typeOfWorkEn(payload.getTypeOfWorkEn())
                .incomePerDay(payload.getIncomePerDay())
                .paidIncomeAt(payload.getPaidIncomeAt())
                .workingHourLimit(payload.getWorkingHourLimit())
                .workingDayPerWeek(payload.getWorkingDayPerWeek())
                .employmentPeriodMonth(payload.getEmploymentPeriodMonth())
                .dayOffWeeklyTh(payload.getDayOffWeeklyTh())
                .dayOffWeeklyEn(payload.getDayOffWeeklyEn())
                .dayOffHolidayTh(payload.getDayOffHolidayTh())
                .dayOffHolidayEn(payload.getDayOffHolidayEn())
                .daysAnnualLeaveTh(payload.getDaysAnnualLeaveTh())
                .daysAnnualLeaveEn(payload.getDaysAnnualLeaveEn())
                .overtimeRateTh(payload.getOvertimeRateTh())
                .overtimeRateEn(payload.getOvertimeRateEn())
                .holidayOvertimeRateTh(payload.getHolidayOvertimeRateTh())
                .holidayOvertimeRateEn(payload.getHolidayOvertimeRateEn())
                .createdAt(LocalDateTime.now())
                .employeeSnapshot(employeeSnapshotJson)
                .employerSnapshot(employerSnapshotJson)
                .employerId(payload.getEmployerId())
                .employeeId(payload.getPassportNo())
                .build();

        return employmentContractRepository.save(contract);
    }

    public EmploymentContractResponse getEmploymentContractById(String id) {
        EmploymentContract contract = employmentContractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment contract not found with ID: " + id));

        return EmploymentContractResponse.builder()
                .id(contract.getId())
                .typeOfWorkTh(contract.getTypeOfWorkTh())
                .typeOfWorkEn(contract.getTypeOfWorkEn())
                .incomePerDay(contract.getIncomePerDay())
                .paidIncomeAt(contract.getPaidIncomeAt())
                .workingHourLimit(contract.getWorkingHourLimit())
                .workingDayPerWeek(contract.getWorkingDayPerWeek())
                .employmentPeriodMonth(contract.getEmploymentPeriodMonth())
                .dayOffWeeklyTh(contract.getDayOffWeeklyTh())
                .dayOffWeeklyEn(contract.getDayOffWeeklyEn())
                .dayOffHolidayTh(contract.getDayOffHolidayTh())
                .dayOffHolidayEn(contract.getDayOffHolidayEn())
                .daysAnnualLeaveTh(contract.getDaysAnnualLeaveTh())
                .daysAnnualLeaveEn(contract.getDaysAnnualLeaveEn())
                .overtimeRateTh(contract.getOvertimeRateTh())
                .overtimeRateEn(contract.getOvertimeRateEn())
                .holidayOvertimeRateTh(contract.getHolidayOvertimeRateTh())
                .holidayOvertimeRateEn(contract.getHolidayOvertimeRateEn())
                .createdAt(contract.getCreatedAt())
                .employeeSnapshot(contract.getEmployeeSnapshot())
                .employerSnapshot(contract.getEmployerSnapshot())
                .build();
    }

    public List<EmploymentContractHistoryDTO> getEmploymentContractHistory(String passportNo, String employerId, Integer limit) {
        // employerId is required for history
        if (employerId == null || employerId.isBlank()) {
            throw new IllegalArgumentException("Employer ID is required for fetching employment contract history");
        }

        int fetchLimit = (limit != null && limit > 0) ? limit : 3;

        List<EmploymentContract> contracts = employmentContractRepository.findLatestByEmployeeAndEmployer(passportNo, employerId, fetchLimit);

        return contracts.stream()
                .map(contract -> EmploymentContractHistoryDTO.builder()
                        .id(contract.getId())
                        .createdAt(contract.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
