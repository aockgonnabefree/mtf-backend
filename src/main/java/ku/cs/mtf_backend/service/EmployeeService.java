package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateEmployeePayload;
import ku.cs.mtf_backend.dto.request.UpdateEmployeePayload;
import ku.cs.mtf_backend.dto.response.EmployeeStatisticsResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.EmployeeRepository;
import ku.cs.mtf_backend.repository.EmployerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployerRepository employerRepository;
    private AddressService addressService;
    private DocumentService documentService;
    private EmploymentService employmentService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployerRepository employerRepository,
                           AddressService addressService,
                           DocumentService documentService,
                           EmploymentService employmentService) {
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.addressService = addressService;
        this.documentService = documentService;
        this.employmentService = employmentService;
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
}
