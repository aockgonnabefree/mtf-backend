package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Work;

import java.util.Optional;

public interface WorkRepository {
    Work save(Work work);
    Optional<Work> findById(String id);
    int updateStep(String workId, int stepIndex, String stepName);
    int updateStatus(String workId, String status);
}
