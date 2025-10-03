package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Employer;

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
}
