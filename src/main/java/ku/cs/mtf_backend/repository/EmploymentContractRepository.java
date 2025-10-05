package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.EmploymentContract;

import java.util.List;
import java.util.Optional;

public interface EmploymentContractRepository {
    EmploymentContract save(EmploymentContract contract);
    Optional<EmploymentContract> findById(String id);
    List<EmploymentContract> findLatestByEmployeeAndEmployer(String employeeId, String employerId, int limit);
    List<EmploymentContract> findLatestByEmployeeId(String employeeId, int limit);
}
