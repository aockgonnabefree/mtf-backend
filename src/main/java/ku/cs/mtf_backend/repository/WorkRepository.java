package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.dto.projection.WorkSummary;
import ku.cs.mtf_backend.entity.Work;

import java.util.List;
import java.util.Optional;

public interface WorkRepository {
    Work save(Work work);
    Optional<Work> findById(String id);
    int updateStep(String workId, int stepIndex, String stepName);
    int updateStatus(String workId, String status);

    // Statistics
    long countAll();
    long countByStatus(String status);

    // List with filters and pagination
    List<WorkSummary> findAllSummariesWithFilters(String employerName, String workType, String status,
                                                   int offset, int limit);
    long countWithFilters(String employerName, String workType, String status);
}
