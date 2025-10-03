package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Employee;

public interface EmployeeRepository {
    boolean existsByPassportNumber(String passportNumber);
    Employee save(Employee employee);
}
