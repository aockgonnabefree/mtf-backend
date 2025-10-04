package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Employee;

import java.util.Optional;

public interface EmployeeRepository {
    boolean existsByPassportNumber(String passportNumber);
    Employee save(Employee employee);
    boolean existsById(String passportNo);
    Optional<Employee> findById(String passportNo);
    Employee update(Employee employee);

    // Statistics methods
    long countAll();
    long countWithExpiredDocuments();
    long countWithExpiringSoonDocuments(int daysThreshold);
}
