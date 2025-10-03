package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateEmployeePayload;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Employee;
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
}
