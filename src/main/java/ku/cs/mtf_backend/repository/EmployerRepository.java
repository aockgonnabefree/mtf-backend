package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.dto.projection.EmployerSummary;
import ku.cs.mtf_backend.dto.response.EmployerSelectDTO;
import ku.cs.mtf_backend.entity.Employer;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository {
    boolean existsById(String id);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Employer save(Employer employer);
    Optional<Employer> findById(String id);
    Optional<Employer> findByEmail(String email);
    Optional<Employer> findByPhoneNumber(String phoneNumber);
    Employer update(Employer employer);

    // Statistics methods
    long countAll();
    long countByStatus(String status);

    // Pagination and filtering
    List<EmployerSummary> findAllWithPagination(Integer page, Integer size, String nameFilter, String statusFilter);

    // Get all employers for select/lookup
    List<EmployerSelectDTO> findAllEmployersForSelect();
}
