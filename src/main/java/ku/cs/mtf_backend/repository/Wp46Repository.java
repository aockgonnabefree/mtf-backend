package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Wp46;

import java.util.List;
import java.util.Optional;

public interface Wp46Repository {
    Wp46 save(Wp46 wp46);
    Optional<Wp46> findById(String id);
    List<Wp46> findLatestByEmployeeAndEmployer(String employeeId, String employerId, int limit);
    List<Wp46> findLatestByEmployeeId(String employeeId, int limit);
}
