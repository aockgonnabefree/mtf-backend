package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Employment;

import java.util.Optional;

public interface EmploymentRepository {
    void save(Employment employment);
    Optional<Employment> findActiveByEmployeeId(String employeeId);
    Optional<Employment> findByEmployerIdAndEmployeeId(String employerId, String employeeId);
    int updateStatus(String employerId, String employeeId, String status);
    Optional<Employment> findByEmployeeIdAndStatus(String employeeId, String status);
    boolean existsByEmployerAndEmployee(String employerId, String employeeId);
}
