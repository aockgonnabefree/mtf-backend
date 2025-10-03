package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.entity.Employment;
import ku.cs.mtf_backend.repository.EmploymentRepository;
import org.springframework.stereotype.Service;

@Service
public class EmploymentService {
    private final EmploymentRepository employmentRepository;

    public EmploymentService(EmploymentRepository employmentRepository) {
        this.employmentRepository = employmentRepository;
    }

    public void createEmploymentRelationship(String employerId, String employeeId, String status) {
        Employment employment = Employment.builder()
                .employerId(employerId)
                .employeeId(employeeId)
                .status(status)
                .build();

        employmentRepository.save(employment);
    }
}
