package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.projection.EmployeeSummary;
import ku.cs.mtf_backend.dto.request.CreateEmployeePayload;
import ku.cs.mtf_backend.dto.request.UpdateEmployeePayload;
import ku.cs.mtf_backend.dto.response.EmployeeStatisticsResponse;
import ku.cs.mtf_backend.dto.response.EmployeeSummaryDTO;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Document;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.DocumentRepository;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployerRepository employerRepository;
    private AddressService addressService;
    private DocumentService documentService;
    private EmploymentService employmentService;
    private DocumentRepository documentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployerRepository employerRepository,
                           AddressService addressService,
                           DocumentService documentService,
                           EmploymentService employmentService,
                           DocumentRepository documentRepository) {
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.addressService = addressService;
        this.documentService = documentService;
        this.employmentService = employmentService;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public Employee createEmployee(CreateEmployeePayload payload) {
        // 1. Validate input data
        if (!employerRepository.existsById(payload.getEmployerId())) {
            throw new IllegalArgumentException("Employer with ID " + payload.getEmployerId() + " not found.");
        }
        if (employeeRepository.existsByPassportNumber(payload.getPassportNo())) {
            throw new IllegalArgumentException("Employee with Passport No " + payload.getPassportNo() + " already exists.");
        }

        // 2. Delegate address creation to AddressService
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 3. Create and save the core Employee entity
        Employee employeeToSave = Employee.builder()
                .passportNumber(payload.getPassportNo())
                .firstname(payload.getFirstName())
                .lastname(payload.getLastName())
                .nationality(payload.getNationality())
                .bloodType(payload.getBloodType())
                .status(payload.getStatus())
                .addressId(address.getId())
                .build();
        Employee savedEmployee = employeeRepository.save(employeeToSave);

        // 4. Delegate document creation to DocumentService
        documentService.createAndSaveDocuments(savedEmployee.getPassportNumber(), payload.getDocuments());

        // 5. Delegate employment relationship creation to EmploymentService
        employmentService.createEmploymentRelationship(
                payload.getEmployerId(),
                savedEmployee.getPassportNumber(),
                "ACTIVE" // Default status for new employment is active
        );

        return savedEmployee;
    }

    @Transactional
    public Employee updateEmployee(String passportNo, UpdateEmployeePayload payload) {
        // 1. ค้นหาลูกจ้างที่จะแก้ไข ถ้าไม่เจอจะโยน Exception
        Employee existingEmployee = employeeRepository.findById(passportNo)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with passport number: " + passportNo));

        // 2. ตรวจสอบว่านายจ้างคนใหม่ที่ระบุมามีตัวตนอยู่จริงหรือไม่
        if (!employerRepository.existsById(payload.getEmployerId())) {
            throw new ResourceNotFoundException("Employer not found with id: " + payload.getEmployerId());
        }

        // 3. จัดการเรื่องที่อยู่ (หาหรือสร้างใหม่) โดยเรียกใช้ AddressService
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 4. อัปเดตข้อมูลพื้นฐานของ Employee object
        existingEmployee.setFirstname(payload.getFirstName());
        existingEmployee.setLastname(payload.getLastName());
        existingEmployee.setNationality(payload.getNationality());
        existingEmployee.setBloodType(payload.getBloodType());
        existingEmployee.setStatus(payload.getStatus());
        existingEmployee.setAddressId(address.getId());

        // 5. บันทึกการเปลี่ยนแปลงข้อมูลพื้นฐานของ Employee ลง DB
        Employee updatedEmployee = employeeRepository.update(existingEmployee);

        // 6. ซิงค์ข้อมูลเอกสารทั้งหมด (Update, Insert, Delete) โดยเรียกใช้ DocumentService
        documentService.syncDocumentsForEmployee(passportNo, payload.getDocuments());

        // 7. จัดการ Logic การเปลี่ยนนายจ้าง โดยเรียกใช้ EmploymentService
        employmentService.changeEmployerForEmployee(passportNo, payload.getEmployerId());

        return updatedEmployee;
    }

    public EmployeeStatisticsResponse getStatistics(Integer daysThreshold) {
        // 1. นับจำนวนลูกจ้างทั้งหมด
        long totalEmployees = employeeRepository.countAll();

        // 2. นับลูกจ้างที่มีเอกสารหมดอายุ (กรณีแย่ที่สุด)
        long expiredDocumentEmployees = employeeRepository.countWithExpiredDocuments();

        // 3. นับลูกจ้างที่มีเอกสารใกล้หมดอายุ (ไม่รวมคนที่มีเอกสารหมดอายุแล้ว)
        long expiringSoonEmployees = employeeRepository.countWithExpiringSoonDocuments(daysThreshold);

        // 4. นับลูกจ้างที่เอกสารใช้งานได้หมด = ทั้งหมด - หมดอายุ - ใกล้หมดอายุ
        long validDocumentEmployees = totalEmployees - expiredDocumentEmployees - expiringSoonEmployees;

        return EmployeeStatisticsResponse.builder()
                .totalEmployees(totalEmployees)
                .validDocumentEmployees(validDocumentEmployees)
                .expiringSoonEmployees(expiringSoonEmployees)
                .expiredDocumentEmployees(expiredDocumentEmployees)
                .build();
    }

    public PageResponse<EmployeeSummaryDTO> getEmployeesWithPagination(Integer page, Integer size, String nameContains, String status, Integer daysThreshold) {
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

        // 2. Fetch employee data from repository
        List<EmployeeSummary> employees = employeeRepository.findAllWithPagination(page, size, nameContains, status);

        if (employees.isEmpty()) {
            return PageResponse.<EmployeeSummaryDTO>builder()
                    .content(new ArrayList<>())
                    .totalElements(0L)
                    .totalPages(0)
                    .currentPage(page)
                    .pageSize(size)
                    .build();
        }

        // 3. Get employee IDs
        List<String> employeeIds = employees.stream()
                .map(EmployeeSummary::getPassportNumber)
                .collect(Collectors.toList());

        // 4. Fetch documents for all employees in one query
        Map<String, List<Document>> documentsByEmployee = documentRepository.findDocumentsByEmployeeIds(employeeIds);

        // 5. Build DTOs with document statuses
        List<EmployeeSummaryDTO> employeeDTOs = employees.stream()
                .map(e -> {
                    List<Document> docs = documentsByEmployee.getOrDefault(e.getPassportNumber(), new ArrayList<>());
                    Map<String, String> documentStatuses = buildDocumentStatuses(docs, daysThreshold);

                    return EmployeeSummaryDTO.builder()
                            .passportNumber(e.getPassportNumber())
                            .fullName(e.getFullName())
                            .currentEmployer(e.getCurrentEmployer())
                            .status(e.getStatus())
                            .documentStatuses(documentStatuses)
                            .build();
                })
                .collect(Collectors.toList());

        // 6. Calculate pagination info
        long totalElements = employeeRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<EmployeeSummaryDTO>builder()
                .content(employeeDTOs)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    private Map<String, String> buildDocumentStatuses(List<Document> documents, int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);

        // Initialize all document types
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("ใบรับรองแพทย์", "NOT_HAVE");
        statuses.put("ใบอนุญาตทำงาน", "NOT_HAVE");
        statuses.put("ประกันสุขภาพ", "NOT_HAVE");
        statuses.put("เอกสาร CI", "NOT_HAVE");
        statuses.put("บัตรชมพู", "NOT_HAVE");

        // Update with actual document statuses
        for (Document doc : documents) {
            String status;
            if (doc.getExpiryDate().isBefore(today)) {
                status = "EXPIRED";
            } else if (doc.getExpiryDate().isAfter(today) && doc.getExpiryDate().isBefore(thresholdDate)) {
                status = "EXPIRING_SOON";
            } else {
                status = "VALID";
            }
            statuses.put(doc.getType(), status);
        }

        return statuses;
    }
}
