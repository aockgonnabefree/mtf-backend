package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Employer;

public interface EmployerRepository {
    boolean existsById(String id);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Employer save(Employer employer);
}
